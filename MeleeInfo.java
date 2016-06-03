package server.model.players.combat;

import server.model.players.Client;
import server.model.players.CombatAssistant;
import server.Config;
import server.model.npcs.NPCHandler;

public class MeleeInfo {

	public static int calculateAttack(Client c) {
		int attackLevel = c.playerLevel[0];
		//2, 5, 11, 18, 19
		if (c.prayerActive[2]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.05;
		} else if (c.prayerActive[7]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.1;
		} else if (c.prayerActive[15]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.15;
		} else if (c.prayerActive[24]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.18;
		} else if (c.prayerActive[25]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.22;
		} else if(c.curseActive[10]) {//Leech Attack
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.15;
		} else if (c.curseActive[19]) { // turmoil
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.25;
		}
		if (c.fullVoidMelee())
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.1;
		if (c.fullVoidElite())
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.2;
		attackLevel *= c.specAccuracy;
		int i = c.playerBonus[bestMeleeAtk(c)];
		i += c.bonusAttack;
		if (c.playerEquipment[c.playerAmulet] == 11128 && (c.playerEquipment[c.playerWeapon] == 6528 || c.playerEquipment[c.playerWeapon] == 6525)) {
			i *= 1.20;
		}
		return (int)(attackLevel + (attackLevel * 0.15) + (i + i * 0.15));
	}

	public static int calculateDefence(Client c) {
		int defenceLevel = c.playerLevel[1];
		int i = c.playerBonus[bestMeleeDef(c)];
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
		return (int)(defenceLevel + (defenceLevel * 0.15) + (i * 0.90));
	}

	public static int bestMeleeDef(Client c) {
		if(c.playerBonus[5] > c.playerBonus[6] && c.playerBonus[5] > c.playerBonus[7])
			return 5;
		if(c.playerBonus[6] > c.playerBonus[5] && c.playerBonus[6] > c.playerBonus[7])
			return 6;
		return c.playerBonus[7] <= c.playerBonus[5] || c.playerBonus[7] <= c.playerBonus[6] ? 5 : 7;
	}

	public static int bestMeleeAtk(Client c) {
		if(c.playerBonus[0] > c.playerBonus[1] && c.playerBonus[0] > c.playerBonus[2])
			return 0;
		if(c.playerBonus[1] > c.playerBonus[0] && c.playerBonus[1] > c.playerBonus[2])
			return 1;
		return c.playerBonus[2] <= c.playerBonus[1] || c.playerBonus[2] <= c.playerBonus[0] ? 0 : 2;
	}

	public static int maximumHit(Client c) {
		double maxHit = 0;
		//int strBonus = c.playerBonus[10];
		int strength = c.playerLevel[2];
		int realStrength = c.getLevelForXP(c.playerXP[2]);
		if(c.prayerActive[1]) {
			strength += (int)(realStrength * .05);
		} else if(c.prayerActive[6]) {
			strength += (int)(realStrength * .10);
		} else if(c.prayerActive[14]) {
			strength += (int)(realStrength * .15);
		} else if(c.prayerActive[24]) {
			strength += (int)(realStrength * .18);
		} else if(c.prayerActive[25]) {
			strength += (int)(realStrength * .23);
		} else if (c.curseActive[14]) //Leech Strength
			strength += (int)(realStrength * .15);
		else if(c.curseActive[19]) // turmoil
			strength += (int)(realStrength * .32) ;

		maxHit += 1.05D + (double)(CombatAssistant.datStr * strength) * 0.00185D;
		maxHit += (double)strength * 0.09D;

		if (c.playerEquipment[c.playerWeapon] == 4718
				&& c.playerEquipment[c.playerHat] == 4716
				&& c.playerEquipment[c.playerChest] == 4720
				&& c.playerEquipment[c.playerLegs] == 4722) {
			double dharokMultiplier = ((1 - ((float) c.playerLevel[3] / (float) c
					.getPA().getLevelForXP(c.playerXP[3]))) * 0.95) + 1.05;															// 1.05
			maxHit *= dharokMultiplier;
		}
		if (c.specDamage > 1)
			maxHit = (int) (maxHit * c.specDamage);
		if (maxHit < 0)
			maxHit = 1;
		if (c.fullVoidMelee())
			maxHit = (int) (maxHit * 1.10);// changed from .30
		if (c.slayerHelmetEffect && c.slayerTask != 0)
			maxHit = (int) (maxHit * 1.15);
		if (c.playerEquipment[c.playerAmulet] == 11128
				&& c.playerEquipment[c.playerWeapon] == 6528) {
			maxHit *= 1.20;
		}

		return (int)Math.floor(maxHit);
	}
}