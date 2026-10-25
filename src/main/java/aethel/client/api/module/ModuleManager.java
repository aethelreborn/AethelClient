package aethel.client.api.module;

import net.minecraft.client.Minecraft;
import aethel.client.api.events.annotation.SubscribeEvent;
import aethel.client.api.events.bus.EventBus;
import aethel.client.api.events.impl.TickEvent;
import aethel.client.api.module.impl.combat.AuraModule;
import aethel.client.api.module.impl.combat.AntiBot;
import aethel.client.api.module.impl.combat.AimAssist;
import aethel.client.api.module.impl.combat.AutoCrystal;
import aethel.client.api.module.impl.combat.AutoGApple;
import aethel.client.api.module.impl.combat.AutoSwap;
import aethel.client.api.module.impl.combat.AutoTotem;
import aethel.client.api.module.impl.combat.Criticals;
import aethel.client.api.module.impl.combat.HitBoxModule;
import aethel.client.api.module.impl.combat.HitSound;
import aethel.client.api.module.impl.combat.MaceTarget;
import aethel.client.api.module.impl.combat.NoFriendDamage;
import aethel.client.api.module.impl.combat.NoInteract;
import aethel.client.api.module.impl.combat.TriggerBot;
import aethel.client.api.module.impl.combat.Velocity;
import aethel.client.api.module.impl.movement.*;
import aethel.client.api.module.impl.misc.AutoAuth;
import aethel.client.api.module.impl.misc.AutoDuel;
import aethel.client.api.module.impl.misc.AutoLes;
import aethel.client.api.module.impl.misc.AutoLeave;
import aethel.client.api.module.impl.misc.AutoTpAccept;
import aethel.client.api.module.impl.misc.ClickGuiModule;
import aethel.client.api.module.impl.misc.ClientSounds;
import aethel.client.api.module.impl.misc.SelfDestructModule;
import aethel.client.api.module.impl.combat.AutoClicker;
import aethel.client.api.module.impl.combat.Reach;
import aethel.client.api.module.impl.misc.Timer;
import aethel.client.api.module.impl.misc.AutoFish;
import aethel.client.api.module.impl.movement.Scaffold;
import aethel.client.api.module.impl.movement.NoFall;
import aethel.client.api.module.impl.movement.AntiAFK;
import aethel.client.api.module.impl.movement.SafeWalk;
import aethel.client.api.module.impl.movement.Step;
import aethel.client.api.module.impl.player.AutoArmor;
import aethel.client.api.module.impl.misc.BaseFinder;
import aethel.client.api.module.impl.visual.FullBright;
import aethel.client.api.module.impl.misc.DeathCoords;
import aethel.client.api.module.impl.misc.ElytraHelper;
import aethel.client.api.module.impl.misc.JoinerHelper;
import aethel.client.api.module.impl.misc.ServerHelper;
import aethel.client.api.module.impl.misc.UseTracker;
import aethel.client.api.module.impl.player.AHHelper;
import aethel.client.api.module.impl.player.AutoRespawn;
import aethel.client.api.module.impl.player.AutoTool;
import aethel.client.api.module.impl.player.ChestStealer;
import aethel.client.api.module.impl.player.ClickPearl;
import aethel.client.api.module.impl.player.FakePing;
import aethel.client.api.module.impl.player.FastBreak;
import aethel.client.api.module.impl.misc.ServerRPSpoofer;
import aethel.client.api.module.impl.player.NoEntityTrace;
import aethel.client.api.module.impl.player.NoDelay;
import aethel.client.api.module.impl.player.NoPush;
import aethel.client.api.module.impl.player.NameProtect;
import aethel.client.api.module.impl.player.ItemScroller;
import aethel.client.api.module.impl.player.FreeLook;
import aethel.client.api.module.impl.player.FreeCam;
import aethel.client.api.module.impl.player.OpenWalls;
import aethel.client.api.module.impl.player.WindJump;
import aethel.client.api.module.impl.visual.Ambience;
import aethel.client.api.module.impl.visual.Arrows;
import aethel.client.api.module.impl.visual.AspectRatio;
import aethel.client.api.module.impl.visual.BlockESP;
import aethel.client.api.module.impl.visual.DashTrail;
import aethel.client.api.module.impl.visual.ESP;
import aethel.client.api.module.impl.visual.FogBlur;
import aethel.client.api.module.impl.visual.Hands;
import aethel.client.api.module.impl.visual.Hud;
import aethel.client.api.module.impl.visual.ItemPhysics;
import aethel.client.api.module.impl.visual.KillEffect;
import aethel.client.api.module.impl.visual.NoRender;
import aethel.client.api.module.impl.visual.Particles;
import aethel.client.api.module.impl.visual.Predictions;
import aethel.client.api.module.impl.visual.SeeInvisible;
import aethel.client.api.module.impl.visual.SwingAnimation;
import aethel.client.api.module.impl.visual.TargetESP;
import aethel.client.api.module.impl.visual.ViewModel;
import aethel.client.api.settings.bind.KeyBind;
import aethel.client.utils.inventory.InventoryFlowManager;
import aethel.client.utils.network.Network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class ModuleManager {
    private final EventBus eventBus;
    private final List<Module> modules = new ArrayList<>();
    private final Map<String, Module> byName = new HashMap<>();
    private final Map<Class<? extends Module>, Module> byType = new HashMap<>();
    private final Map<ModuleCategory, List<Module>> byCategory = new EnumMap<>(ModuleCategory.class);
    private final Map<ModuleCategory, List<Module>> byCategoryViews = new EnumMap<>(ModuleCategory.class);
    private Collection<Module> modulesView = List.of();
    private final Map<Module, Boolean> bindStates = new IdentityHashMap<>();
    private Runnable dirtyListener = () -> {};
    private boolean destroyed;

    public ModuleManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void init() {
        registerDefaults();
        eventBus.register(this);
    }

    public void setDirtyListener(Runnable dirtyListener) {
        this.dirtyListener = dirtyListener == null ? () -> {} : dirtyListener;
    }

    public void register(Module module) {
        String key = normalize(module.getName());
        if (byName.containsKey(key)) {
            return;
        }
        modules.add(module);
        byName.put(key, module);
        byType.putIfAbsent(module.getClass(), module);
        byCategory.computeIfAbsent(module.getCategory(), ignored -> new ArrayList<>()).add(module);
        module.addStateListener(changedModule -> dirtyListener.run());
        module.initialize(eventBus);
        sortViews();
    }

    public Collection<Module> getModules() {
        return modulesView;
    }

    public List<Module> getByCategory(ModuleCategory category) {
        return byCategoryViews.getOrDefault(category, List.of());
    }

    public Optional<Module> getByName(String name) {
        return Optional.ofNullable(byName.get(normalize(name)));
    }

    public <T extends Module> Optional<T> getByType(Class<T> type) {
        Module module = byType.get(type);
        if (type.isInstance(module)) {
            return Optional.of(type.cast(module));
        }
        for (Module candidate : modules) {
            if (type.isInstance(candidate)) {
                return Optional.of(type.cast(candidate));
            }
        }
        return Optional.empty();
    }

    public boolean isEnabled(Class<? extends Module> type) {
        return getByType(type).filter(Module::isEnabled).isPresent();
    }

    public Optional<AuraModule> getAura() {
        return getByType(AuraModule.class);
    }

    public Optional<AuraModule> getKillAura() {
        return getAura();
    }

    public Optional<HitBoxModule> getHitBox() {
        return getByType(HitBoxModule.class);
    }

    public boolean isHitBoxEnabled() {
        return isEnabled(HitBoxModule.class);
    }

    public Optional<NoInteract> getNoInteract() {
        return getByType(NoInteract.class);
    }

    public boolean isNoInteractEnabled() {
        return isEnabled(NoInteract.class);
    }

    public Optional<ServerRPSpoofer> getServerRPSpoofer() {
        return getByType(ServerRPSpoofer.class);
    }

    public boolean isServerRPSpooferEnabled() {
        return isEnabled(ServerRPSpoofer.class);
    }

    @SubscribeEvent
    private void onPreTick(TickEvent.Pre event) {
        InventoryFlowManager.update();
    }

    @SubscribeEvent
    private void onTick(TickEvent.Post event) {
        if (destroyed) {
            return;
        }
        Minecraft client = event.getClient();
        Network.tick();
        handleBinds(client);
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick(client);
            }
        }
    }

    private void registerDefaults() {
        register(new AntiBot());
        register(new AimAssist());
        register(new AuraModule());
        register(new AutoCrystal());
        register(new AutoGApple());
        register(new AutoSwap());
        register(new AutoTotem());
        register(new Criticals());
        register(new HitBoxModule());
        register(new MaceTarget());
        register(new HitSound());
        register(new NoFriendDamage());
        register(new NoInteract());
        register(new TriggerBot());
        register(new Velocity());

        register(new AutoSprint());
        register(new Fly());
        register(new ElytraTarget());
        register(new NoWeb());
        register(new NoSlow());
        register(new InventoryMove());
        register(new Spider());
        register(new Speed());
        register(new Strafe());
        register(new SuperFireWork());
        register(new TargetStrafe());
        register(new AirStuck());
        register(new Ambience());
        register(new Arrows());
        register(new AspectRatio());
        register(new DashTrail());
        register(new BlockESP());
        register(new ESP());
        register(new FogBlur());
        register(new Hands());
        register(new Hud());
        register(new ItemPhysics());
        register(new KillEffect());
        register(new Particles());
        register(new Predictions());
        register(new SeeInvisible());
        register(new SwingAnimation());
        register(new ViewModel());
        register(new NoRender());
        register(new TargetESP());

        register(new WindJump());
        register(new OpenWalls());
        register(new NoPush());
        register(new NoEntityTrace());
        register(new NoDelay());
        register(new FastBreak());
        register(new FakePing());
        register(new NameProtect());
        register(new ItemScroller());
        register(new FreeLook());
        register(new FreeCam());
        register(new AHHelper());
        register(new AutoRespawn());
        register(new AutoTool());
        register(new ChestStealer());

        register(new JoinerHelper());
        register(new ServerRPSpoofer());
        register(new AutoAuth());
        register(new AutoDuel());
        register(new AutoLeave());
        register(new AutoTpAccept());
        register(new AutoLes());
        register(new ClientSounds());
        register(new ClickPearl());
        register(new DeathCoords());
        register(new ElytraHelper());
        register(new ServerHelper());
        register(new UseTracker());

        register(new ClickGuiModule());
        register(new SelfDestructModule());

        register(new Derp());
        register(new Twerk());

        register(new Killaura());
        register(new AimAssist());

        register(new Speed());
        register(new NoSlow());

        register(new Step());

        register(new AutoClicker());
        register(new Reach());
        register(new NoFall());
        register(new AntiAFK());
        register(new SafeWalk());
        register(new Step());
        register(new AutoArmor());
        register(new BaseFinder());
        register(new Timer());
        register(new AntiExploit());
        register(new AutoBreak());
        register(new AutoFish());
        register(new Scaffold());
        register(new FullBright());
        register(new ModuleClip());
        register(new ModuleAntiHunger());
        register(new ESP());
        register(new Chams());
        register(new ModuleDankBobbing());
        register(new ModuleHandDerp());
        register(new Fly());
        register(new AntiVoid());
        register(new Scaffold());
        register(new AutoRespawn());
        register(new AutoWalk());
        register(new AutoSprint());
        register(new NoPush());
        register(new SuperFireWork());
        register(new Sneak());
    }

    /**
     * Vape-style destruct: silence all tick handling and disable every
     * module so no client surface remains active until restart.
     */
    public void destroy() {
        destroyed = true;
        bindStates.clear();
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.setEnabled(false);
            }
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    private void handleBinds(Minecraft client) {
        if (client == null || client.getWindow() == null || client.screen != null) {
            bindStates.clear();
            return;
        }

        long handle = client.getWindow().handle();
        for (Module module : modules) {
            // ClickGuiModule is driven by KeyboardHandlerMixin so pressing
            // the bind works both to open and to close the GUI.
            if (module instanceof ClickGuiModule) {
                continue;
            }
            KeyBind bind = module.getBind();
            boolean down = bind.isDown(handle);
            boolean wasDown = bindStates.getOrDefault(module, false);
            if (module.isHoldBind()) {
                // Vape-style hold mode: enabled for exactly as long as the
                // bind is physically held.
                if (down != wasDown) {
                    module.setEnabled(down);
                }
            } else if (down && !wasDown) {
                module.toggle();
            }
            bindStates.put(module, down);
        }
    }

    private void register(Module module, String name, String description, ModuleCategory category) {
        module.configure(name, description, category);
        register(module);
    }

    private void sortViews() {
        modules.sort(Comparator.comparing(module -> module.getName().toLowerCase(Locale.ROOT)));
        for (List<Module> categoryModules : byCategory.values()) {
            categoryModules.sort(Comparator.comparing(module -> module.getName().toLowerCase(Locale.ROOT)));
        }
        modulesView = Collections.unmodifiableList(modules);
        byCategoryViews.clear();
        for (Map.Entry<ModuleCategory, List<Module>> entry : byCategory.entrySet()) {
            byCategoryViews.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
    }

    private String normalize(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT).replace(" ", "");
    }
}
