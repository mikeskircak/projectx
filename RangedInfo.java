package server.model.players.combat;

import server.model.players.Client;
import server.Config;

public class RangedInfo {

	public static boolean checkAmmo(Client c) {
		boolean usingArrows = false;
		boolean usingCross = c.playerEquipment[c.playerWeapon] == 9185 || c.playerEquipment[c.playerWeapon] == 15041;

		for (int arrowId : ARROWS) {
			if(c.playerEquipment[c.playerArrows] == arrowId)
				usingArrows = true;
		}
		
		if (c.usingBow && !usingCross && !RangedInfo.usingCrystalBow(c.playerEquipment[c.playerWeapon])) {
			if (Config.CORRECT_ARROWS) {
				if (usingArrows && correctBowAndArrows(c) != c.playerEquipment[c.playerArrows]) {
					c.sendMessage("You cannot use "+c.getItems().getItemName(c.playerEquipment[c.playerArrows]).toLowerCase()+"s with a "+c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase()+".");
					return false;
				}
			}
			if (!usingArrows) {
				if(c.playerEquipment[c.playerWeapon] == 15241)
					c.sendMessage("You have run out of shots");
				else
					c.sendMessage("You have run out of arrows!");
				return false;
			}
		}
		if (usingCross && !properBolts(c.playerEquipment[c.playerArrows])) {
			c.sendMessage("You must use bolts with a crossbow.");
			c.stopMovement();
			return false;				
		}
		return true;
	}
	
	public static boolean isRanging(Client c) {
		return (c.usingBow || c.usingOtherRangeWeapons) && !c.usingMagic;
	}

	public static int correctBowAndArrows(Client c) {
		switch (c.playerEquipment[c.playerWeapon]) {
		case 839:
		case 841:
			return 882;

		case 843:
		case 845:
			return 884;

		case 847:
		case 849:
			return 886;

		case 851:
		case 853:
			return 888;

		case 855:
		case 857:
			return 890;

		case 859:
		case 861:
			if (c.playerEquipment[c.playerArrows] == 892 || c.playerEquipment[c.playerArrows] == 890 ||
				c.playerEquipment[c.playerArrows] == 888 || c.playerEquipment[c.playerArrows] == 886 || c.playerEquipment[c.playerArrows] == 884)
			return c.playerEquipment[c.playerArrows];

		case 4734:
		case 4935:
		case 4936:
		case 4937:
			return 4740;
		case 15241:
			return 15243;
		case 11235:
			if (c.playerEquipment[c.playerArrows] == 892 || c.playerEquipment[c.playerArrows] == 890 ||
				c.playerEquipment[c.playerArrows] == 888 || c.playerEquipment[c.playerArrows] == 886 || c.playerEquipment[c.playerArrows] == 884 || c.playerEquipment[c.playerArrows] == 882 || c.playerEquipment[c.playerArrows] == 11212)
			return c.playerEquipment[c.playerArrows];
		}
		return -1;
	}

	public static int maximumHit(Client c) {
		int rangeLevel = c.playerLevel[4];
		double modifier = 1.0;
		double wtf = c.specDamage;
		int itemUsed = c.usingBow ? c.lastArrowUsed : c.lastWeaponUsed;

		if (c.prayerActive[3]) {
			modifier += 0.05;
		} else if (c.prayerActive[11]) {
			modifier += 0.11;
		} else if (c.prayerActive[19]) {
			modifier += 0.17;
		} else if (c.curseActive[11]) {
			modifier += 0.06;
		}

		if (c.fullVoidRange())
			modifier += 0.12;

		int rangeStr = getEquipmentStrength(itemUsed);
		double b = modifier * rangeLevel;
		double max = (b + 8) * (rangeStr + 64) / 640;

		if (wtf > 1)
			max = (int) (max * wtf);
		if (max < 1)
			max = 1;
		return (int) Math.floor(max);
	}


	public static boolean properBolts(int arrowId) {
		return arrowId >= 9140 && arrowId <= 9144 || arrowId >= 9240 && arrowId <= 9244;
	}

	public static int calculateAttack(Client c) {
		int attackLevel = c.playerLevel[4];
		attackLevel *= c.specAccuracy;
		if (c.fullVoidRange())
			attackLevel += c.getLevelForXP(c.playerXP[c.playerRanged]) * 0.1;
		if (c.fullVoidElite())
			attackLevel += c.getLevelForXP(c.playerXP[c.playerRanged]) * 0.2;
		if (c.prayerActive[3])
			attackLevel *= 1.05;
		else if (c.prayerActive[11])
			attackLevel *= 1.10;
		else if (c.prayerActive[19])
			attackLevel *= 1.15;
		else if (c.curseActive[11])
			attackLevel += attackLevel * 0.15;
		//dbow spec
		if (c.fullVoidRange())
			attackLevel *= (c.specAccuracy > 1.15) ? 1.75 : 1.15;
		return (int) (attackLevel + (c.playerBonus[4] * 1.95));
	}

	public static int calculateDefence(Client c) {
		int defenceLevel = c.playerLevel[1];
		if (c.prayerActive[0]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.05;
		} else if (c.prayerActive[5]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.1;
		} else if (c.prayerActive[13]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.15;
		} else if (c.prayerActive[24]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.2;
		} else if (c.prayerActive[25]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.25;
		} else if (c.curseActive[13]) { // def pray
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.15;
		} else if (c.curseActive[19]) { // turmoil
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.25;
		}
		return (int) (defenceLevel + c.playerBonus[9] + (c.playerBonus[9] / 2));
	}

	private static int getEquipmentStrength(int i) {
		switch (i) {
		case 4214:
			return 74;
		case 877:
			return 20;
		case 9140:
			return 56;
		case 9141:
			return 74;
		case 9142:
		case 9241:
		case 9240:
			return 92;
		case 9143:
		case 9243:
		case 9242:
			return 110;
		case 9144:
		case 9244:
		case 9245:
			return 135; // was 115
			// bronze to dragon arrows
		case 882:
			return 9;
		case 884:
			return 12;
		case 886:
			return 19;
		case 888:
			return 25;
		case 890:
			return 35;
		case 892:
		case 4740:
			return 79;
		case 11212:
			return 65;
		case 15243:
			return 125;

		// knifes
		case 864:
			return 13;
		case 863:
			return 14;
		case 865:
			return 17;
		case 13883:
			return 160;
		case 13879:
			return 170;
		case 866:
			return 20;
		case 867:
			return 34;
		case 868:
			return 44;
		}
		return 0;
	}

	public static boolean usingCrystalBow(int bowId) {
		return bowId >= 4212 && bowId <= 4223;	
	}

	public static final int[] BOWS = {861,9185,18357,15241,839,845,847,851,855,859,841,843,849,853,857,4212,4214,4215,11235,4216,4217,4218,4219,4220,4221,4222,4223,6724,4734,4934,4935,4936,4937,15041};
	public static final int[] ARROWS = {15243, 882,884,886,888,890,892,4740,11212,9140,9141,4142,9143,9144,9240,9241,9242,9243,9244,9245};
	public static final int[] NO_ARROW_DROP = {15243, 4212,4214,4215,4216,4217,4218,4219,4220,4221,4222,4223,4734,4934,4935,4936,4937};
	public static final int[] Bolts = {
		877, 4740, 9139, 9140, 9141, 9142, 9143, 9144, 9145, 9236, 9237, 9238, 9239, 9240, 9241, 9242, 9243, 9244, 9245}; // Arrows, use with bows
	public static final int[] OTHER_RANGE_WEAPONS = 	{863,864,865,866,867,868,869,806,807,808,809,810,811,825,826,827,828,829,830,800,801,802,803,804,805,6522, 13883, 13879, 15016};
}