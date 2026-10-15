package aethel.client.utils.render.animation.modernfx;

public interface AnimationCalculation {
    default double calculation(double value) {
        return 0;
    }
}