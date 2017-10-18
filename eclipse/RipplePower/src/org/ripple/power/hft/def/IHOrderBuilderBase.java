package org.ripple.power.hft.def;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IHOrderBuilderBase {

	IHOrder commit();

	IHOrder commit(Consumer<IHOrder> onSuccess, BiConsumer<HOrderRejectReasonEnum, IHOrder> onRejected);

}
