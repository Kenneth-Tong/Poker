public class GameRules {
	public static double ante = 5;
	public static int cardsHanded = 5;
	public static String winCondition = "";
	public static int scoreCards(Card[] n) //based on the hand rankings
	{
		if(isRoyalFlush(n))
		{
			return 10; //it's the best value
		}
		else if(isStraightFlush(n))
		{
			return 9;
		}
		else if(isFour(n))
		{
			return 8;
		}
		else if(isFullHouse(n))
		{
			return 7;
		}
		else if(isFlush(n))
		{
			return 6;
		}
		else if(isStraight(n))
		{
			return 5;
		}
		else if(isThree(n))
		{
			return 4;
		}
		else if(two(n) == 2)
		{
			return 3;
		}
		else if(two(n) == 1)
		{
			return 2;
		}
		else //is just a high card
		{
			return 1;
		}
	}
	public static int breakTie(Card[] hand1, Card[] hand2)
	{
		if(hand1[hand1.length - 1].getFaceValue() > hand2[hand2.length - 1].getFaceValue()) //high card
		{
			return 0;
		}
		else if(hand1[hand1.length - 1].getFaceValue() < hand2[hand2.length - 1].getFaceValue()) //high card
		{
			return 1;
		}
		else if(hand1[hand1.length - 2].getFaceValue() > hand2[hand2.length - 2].getFaceValue()) //top two arn't true
		{
			return 0;
		}
		else if(hand1[hand1.length - 2].getFaceValue() < hand2[hand2.length - 2].getFaceValue()) //high card
		{
			return 1;
		}
		else if(hand1[hand1.length - 3].getFaceValue() > hand2[hand2.length - 3].getFaceValue())
		{
			return 0;
		}
		else if(hand1[hand1.length - 3].getFaceValue() < hand2[hand2.length - 3].getFaceValue()) //high card
		{
			return 1;
		}
		else if(hand1[hand1.length - 4].getFaceValue() > hand2[hand2.length - 4].getFaceValue())
		{
			return 0;
		}
		else if(hand1[hand1.length - 4].getFaceValue() < hand2[hand2.length - 4].getFaceValue()) //high card
		{
			return 1;
		}
		else if(hand1[hand1.length - 5].getFaceValue() > hand2[hand2.length - 5].getFaceValue())
		{
			return 0;
		}
		else if(hand1[hand1.length - 5].getFaceValue() < hand2[hand2.length - 5].getFaceValue()) //high card
		{
			return 1;
		}
		return -1; //tie
	}
	/* methods for the checking hand values */
	public static int highCard(Card[] n)
	{
		return n[n.length - 1].getFaceValue();
	}
	public static int two(Card[] n)
	{
		int inARow = 0; //total 2 in a row
		for(int i = 0; i < n.length - 1; i++)
		{
			if(n[i].getFaceValue() == n[i + 1].getFaceValue())
			{
				inARow++;
			}
		}
		return inARow;
	}
	public static boolean isThree(Card[] n)
	{
		for(int i = 0; i < n.length - 2; i++)
		{
			if(n[i].getFaceValue() == n[i + 1].getFaceValue() && n[i + 1].getFaceValue() == n[i + 2].getFaceValue())
			{
				return true;
			}
		}
		return false;
	}
	public static boolean isFour(Card[] n)
	{
		for(int i = 0; i < n.length - 3; i++)
		{
			if(n[i].getFaceValue() == n[i + 1].getFaceValue() && n[i + 1].getFaceValue() == n[i + 2].getFaceValue() && n[i + 2].getFaceValue() == n[i + 3].getFaceValue())
			{
				return true;
			}
		}
		return false;
	}
	public static boolean isStraight(Card[] n)
	{
		int correctNumber = n[0].getFaceValue();
		for(int i = 1; i < n.length; i++)
		{
			if(n[i].getFaceValue() == correctNumber + 1)
			{
				correctNumber++;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	public static boolean isFullHouse(Card[] n)
	{
		return isThree(n) && two(n) == 1; //if it has a three and a two
	}
	public static boolean isStraightFlush(Card[] n)
	{
		String suitCorrect = n[0].getSuit();
		for(int i = 0; i < n.length - 1; i++)
		{
			if(!n[i].getSuit().equals(suitCorrect) || n[i].getFaceValue() + 1 != n[i + 1].getFaceValue()) //only if one of them is off a suit it's not a straight flush
			{
				return false;
			}
		}
		if(n[n.length - 1].getFaceValue() - 1 != n[n.length - 2].getFaceValue())
		{
			return false;
		}
		return true;
	}
	public static boolean isRoyalFlush(Card[] n)
	{
		String suitCorrect = n[0].getSuit();
		int keepCounting = 10; //set it to the value of ten
		if(n[0].getName().equals("Ten")) //only if it is a ten
		{
			for(Card a: n)
			{
				if(!a.getSuit().equals(suitCorrect) || a.getFaceValue() != keepCounting)
				{
					return false;
				}
				keepCounting++; //make sure its ten, jack and so on
			}
		}
		else if(!n[0].getName().equals("Ten"))
		{
			return false;
		}
		return true;
	}
	public static boolean isFlush(Card[] n)
	{
		String suitCorrect = n[0].getSuit();
		for(Card a: n)
		{
			if(!a.getSuit().equals(suitCorrect))
			{
				return false;
			}
		}
		return true;
	}
}