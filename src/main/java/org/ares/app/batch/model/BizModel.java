package org.ares.app.batch.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class BizModel implements Serializable {

	@Getter @Setter private String msg;
	@Getter @Setter private String name;
	private static final long serialVersionUID = 1L;

}
