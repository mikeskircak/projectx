package server.model.players.combat;

import server.model.players.Client;	
import server.Config;	

public class MagicInfo {

	public static int mageAttack(Client c) {
		int attackLevel = c.playerLevel[6];
		if (c.playerEquipment[c.playerWeapon] == 15001)
			attackLevel += 0.15;
		if (c.fullVoidMage())
			attackLevel += c.getLevelForXP(c.playerXP[6]) * 0.2;
		if (c.fullVoidElite())
			attackLevel += c.getLevelForXP(c.playerXP[6]) * 0.3;
		if(c.curseActive[12]) //Leech Magic
			attackLevel += c.getLevelForXP(c.playerXP[6]) * 0.15;
		else if (c.prayerActive[4]) 
			attackLevel *= 1.05;
		else if (c.prayerActive[12])
			attackLevel *= 1.10;
		else if (c.prayerActive[20])
			attackLevel *= 1.15;
		return (int) (attackLevel + (c.playerBonus[3] * 2));
	}

	public static int mageDefence(Client c) {
		int defenceLevel = c.playerLevel[1]/2 + c.playerLevel[6]/2;
		if (c.prayerActive[0]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.05;
		} else if (c.prayerActive[3]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.1;
		} else if (c.prayerActive[9]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.15;
		} else if (c.prayerActive[18]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.2;
		} else if (c.prayerActive[19]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.25;
		} else if (c.curseActive[13]) { // def pray
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.15;
		} else if (c.curseActive[19]) { // turmoil
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.25;
		}
		return (int) (defenceLevel + c.playerBonus[8] + (c.playerBonus[8] / 3));
	}

	public static int magicDamage(Client c) {
		double damage = SPELLS_DATA[c.oldSpellId][6];
		double damageMultiplier = 1;
		if (c.playerLevel[c.playerMagic] > c.getLevelForXP(c.playerXP[6]) && c.getLevelForXP(c.playerXP[6]) >= 95)
			damageMultiplier += .03 * (c.playerLevel[c.playerMagic] - 99);
		else
			damageMultiplier = 1;
		switch (c.playerEquipment[c.playerWeapon]) {
		case 18371: // Gravite Staff
			damageMultiplier += .05;
			break;
		case 4675: // Ancient Staff
		case 4710: // Ahrim's Staff
		case 4862: // Ahrim's Staff
		case 4864: // Ahrim's Staff
		case 4865: // Ahrim's Staff
		case 6914: // Master Wand
		case 8841: // Void Knight Mace
		case 13867: // Zuriel's Staff
		case 13869: // Zuriel's Staff (Deg)
			damageMultiplier += .10;
			break;
		case 15001: // Staff of Light
			damageMultiplier += .15;
			break;
		case 18355: // Chaotic Staff
			damageMultiplier += .20;
			break;
		}
		switch (c.playerEquipment[c.playerAmulet]) {
		case 18333: // Arcane Pulse
			damageMultiplier += .05;
			break;
		case 18334:// Arcane Blast
			damageMultiplier += .10;
			break;
		case 18335:// Arcane Stream
			damageMultiplier += .15;
			break;
		}
		switch (c.playerEquipment[c.playerHat]) {
		case 13350: // Virtus Mask
			damageMultiplier += .03;
			break;
		}
		switch (c.playerEquipment[c.playerChest]) {
		case 13348: // Virtus Body
			damageMultiplier += .06;
			break;
		}
		switch (c.playerEquipment[c.playerLegs]) {
		case 13346: // Virtus Legs
			damageMultiplier += .04;
			break;
		}
		damage *= damageMultiplier;
		return (int)(damage);
	}

	public static int delayForHit(int spellId) {
		switch(SPELLS_DATA[spellId][0]) {			
		case 12891:
			return 4;
		case 12871:
			return 6;
		default:
			return 4;
		}
	}
	
	public static boolean onNormalSpellbook(Client c) {
		return c.playerMagicBook == 0;
	}

	public static boolean onAncientSpellbook(Client c) {
		return c.playerMagicBook == 1;
	}

	public static boolean onLunarSpellbook(Client c) {
		return c.playerMagicBook == 2;
	}
	
	public static final int[][] SPELLS_DATA = { 
		// example {magicId, level req, animation, startGFX, projectile Id, endGFX, maxhit, exp gained, rune 1, rune 1 amount, rune 2, rune 2 amount, rune 3, rune 3 amount, rune 4, rune 4 amount}
		// Modern Spells
		{1152,1,711,90,91,92,2,5,556,1,558,1,0,0,0,0,0}, //wind strike
		{1154,5,711,93,94,95,4,7,555,1,556,1,558,1,0,0,0}, // water strike
		{1156,9,711,96,97,98,6,9,557,2,556,1,558,1,0,0,0},// earth strike
		{1158,13,711,99,100,101,8,11,554,3,556,2,558,1,0,0,0}, // fire strike
		{1160,17,711,117,118,119,9,13,556,2,562,1,0,0,0,0,0}, // wind bolt
		{1163,23,711,120,121,122,10,16,556,2,555,2,562,1,0,0,0}, // water bolt
		{1166,29,711,123,124,125,11,20,556,2,557,3,562,1,0,0,0}, // earth bolt
		{1169,35,711,126,127,128,12,22,556,3,554,4,562,1,0,0,0}, // fire bolt
		{1172,41,711,132,133,134,13,25,556,3,560,1,0,0,0,0,0}, // wind blast
		{1175,47,711,135,136,137,14,28,556,3,555,3,560,1,0,0,0}, // water blast
		{1177,53,711,138,139,140,15,31,556,3,557,4,560,1,0,0,0}, // earth blast
		{1181,59,711,129,130,131,16,35,556,4,554,5,560,1,0,0,0}, // fire blast
		{1183,62,711,158,159,160,17,36,556,5,565,1,0,0,0,0,0}, // wind wave
		{1185,65,711,161,162,163,18,37,556,5,555,7,565,1,0,0,0},  // water wave
		{1188,70,711,164,165,166,19,40,556,5,557,7,565,1,0,0,0}, // earth wave
		{1189,75,711,155,156,157,20,42,556,5,554,7,565,1,0,0,0}, // fire wave
		{1153,3,716,102,103,104,0,13,555,3,557,2,559,1,0,0,0},  // confuse
		{1157,11,716,105,106,107,0,20,555,3,557,2,559,1,0,0,0},  // weaken
		{1161,19,716,108,109,110,0,29,555,2,557,3,559,1,0,0,0}, // curse
		{1542,66,729,167,168,169,0,76,557,5,555,5,566,1,0,0,0}, // vulnerability
		{1543,73,729,170,171,172,0,83,557,8,555,8,566,1,0,0,0}, // enfeeble
		{1562,80,729,173,174,107,0,90,557,12,555,12,556,1,0,0,0},  // stun
		{1572,20,711,177,178,181,0,30,557,3,555,3,561,2,0,0,0}, // bind
		{1582,50,711,177,178,180,2,60,557,4,555,4,561,3,0,0,0}, // snare
		{1592,79,711,177,178,179,4,90,557,5,555,5,561,4,0,0,0}, // entangle
		{1171,39,724,145,146,147,15,25,556,2,557,2,562,1,0,0,0},  // crumble undead
		{1539,50,708,87,88,89,25,42,554,5,560,1,0,0,0,0,0}, // iban blast
		{12037,50,1576,327,328,329,19,30,560,1,558,4,0,0,0,0,0}, // magic dart
		{1190,60,811,0,0,76,20,60,554,2,565,2,556,4,0,0,0}, // sara strike
		{1191,60,811,0,0,77,20,60,554,1,565,2,556,4,0,0,0}, // cause of guthix
		{1192,60,811,0,0,78,20,60,554,4,565,2,556,1,0,0,0}, // flames of zammy
		{12445,85,10503,1841,1842,1843,0,65,563,1,562,1,560,1,0,0,0}, // teleblock

		// Ancient Spells
		{12939,50,1978,0,384,385,13,30,560,2,562,2,554,1,556,1, 1}, // smoke rush
		{12987,52,1978,0,378,379,14,31,560,2,562,2,566,1,556,1, 1}, // shadow rush
		{12901,56,1978,0,0,373,15,33,560,2,562,2,565,1,0,0, 1},  // blood rush
		{12861,58,1978,0,360,361,16,34,560,2,562,2,555,2,0,0,  1},  // ice rush
		{12963,62,1979,0,0,389,19,36,560,2,562,4,556,2,554,2, 1}, // smoke burst
		{13011,64,1979,0,0,382,20,37,560,2,562,4,556,2,566,2, 1}, // shadow burst 
		{12919,68,1979,0,0,376,21,39,560,2,562,4,565,2,0,0, 1},  // blood burst
		{12881,70,1979,0,0,363,22,40,560,2,562,4,555,4,0,0,1}, // ice burst
		{12951,74,1978,0,386,387,23,42,560,2,554,2,565,2,556,2, 1}, // smoke blitz
		{12999,76,1978,0,380,381,24,43,560,2,565,2,556,2,566,2, 1}, // shadow blitz
		{12911,80,1978,0,374,375,25,45,560,2,565,4,0,0,0,0, 1}, // blood blitz
		{12871,82,1978,366,0,367,26,46,560,2,565,2,555,3,0,0, 1}, // ice blitz
		{12975,86,1979,0,0,391,27,48,560,4,565,2,556,4,554,4, 1}, // smoke barrage
		{13023,88,1979,0,0,383,28,49,560,4,565,2,556,4,566,3, 1}, // shadow barrage
		{12929,92,1979,0,0,377,29,51,560,4,565,4,566,1,0,0, 1},  // blood barrage
		{12891,94,1979,0,0,369,30,52,560,4,565,2,555,6,0,0, 1}, // ice barrage
		{-1,80,811,301,0,0,0,0,554,3,565,3,556,3,0,0, 1}, // charge
		{-1,21,712,1693,0,0,0,10,554,3,561,1,0,0,0,0, 1}, // low alch
		{-1,55,713,1693,0,0,0,20,554,5,561,1,0,0,0,0, 1}, // high alch
		//{-1,33,728,142,143,144,0,35,556,1,563,1,0,0,0,0, 1} // telegrab
	};
}