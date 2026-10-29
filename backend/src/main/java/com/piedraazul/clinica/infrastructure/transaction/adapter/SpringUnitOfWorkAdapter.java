package com.piedraazul.clinica.infrastructure.transaction.adapter;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.piedraazul.clinica.domain.port.out.UnidadDeTrabajoPort;

@Component
public class SpringUnitOfWorkAdapter implements UnidadDeTrabajoPort {

	private final TransactionTemplate transactionTemplate;

	public SpringUnitOfWorkAdapter(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	public <T> T ejecutar(Supplier<T> operation) {
		return transactionTemplate.execute(status -> operation.get());
	}
}
