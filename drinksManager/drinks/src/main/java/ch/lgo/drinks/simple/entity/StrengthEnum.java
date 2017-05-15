package ch.lgo.drinks.simple.entity;

import java.util.Arrays;

public enum StrengthEnum {
	N_A(0),
	LOW(1),
	MILD(2),
	STRONG(3),
	VERY_STRONG(4),
	UBER(5);
	
	private int rank;

	private StrengthEnum (int rank) {
		this.rank = rank;
	}
	
	public int getRank() {
		return rank;
	}
	
	public static StrengthEnum getStrengthByRank(int rank) {
		return Arrays.stream(StrengthEnum.values())
			.filter(strength -> strength.getRank() == rank)
			.findFirst()
			.get();
	}
}
