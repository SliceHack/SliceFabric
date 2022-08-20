package com.sliceclient.event.events;

import com.sliceclient.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;

@Getter @Setter
@AllArgsConstructor
public class Event2D extends Event {
    private MatrixStack matrixStack;
    private float tickDelta;
}
