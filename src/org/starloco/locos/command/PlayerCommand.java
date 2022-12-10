package org.starloco.locos.command;

public class PlayerCommand {

	private final String[] name;
	private final int type;
	private final String args;
	private final int price;
	private final boolean vip;
	private final String condition;
	
	public PlayerCommand(String name, int type, String args, int price, boolean vip, String condition) {
		this.name = name.split("\\|");
		this.type = type;
		this.args = args;
		this.price = price;
		this.vip = vip;
		this.condition = condition;
	}

	public String[] getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public String getArgs() {
		return args;
	}

	public int getPrice() {
		return price;
	}

	public boolean isVip() {
		return vip;
	}

	public String getCondition() {
		return condition;
	}
	
	
	
}
