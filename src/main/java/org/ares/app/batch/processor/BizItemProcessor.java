package org.ares.app.batch.processor;

import org.ares.app.batch.model.BizModel;
import org.springframework.batch.item.ItemProcessor;

public class BizItemProcessor implements ItemProcessor<BizModel, BizModel> {

	@Override
	public BizModel process(BizModel item) throws Exception {
		return item;
	}

}
