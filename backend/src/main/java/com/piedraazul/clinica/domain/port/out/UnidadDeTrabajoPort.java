package com.piedraazul.clinica.domain.port.out;

import java.util.function.Supplier;

public interface UnidadDeTrabajoPort {
	<T> T ejecutar(Supplier<T> operation);

	static <T> T ejecutarDirectamente(Supplier<T> operation) {
		return operation.get();
	}
}
