public class AIPlayer extends HumanPlayer{
	public AIPlayer(String n, int max) {
		super(n, max);
		setBot(true);
	}
	public int matchBet(double a) //if triple money no take
	{
		if(getMoney() < a * 3 && GameRules.scoreCards(getCards()) <= 2) //rarely do they fold
		{
			return 1; //fold
		}
		else if(GameRules.scoreCards(getCards()) > 3)
		{
			return 3;
		}
		else
		{
			return 2;
		}
	}
	public double makeBet()
	{
		double moneyBet = 0;
		int result = GameRules.scoreCards(getCards()); 
		int randomRaise = (int) (Math.random() * 4); //sometimes it bluffs
		if(randomRaise == 2)
		{
			result = 10;
		}
		if(result <= 2)
		{
			moneyBet = getMoney() * 0.2;
		}
		else if(result > 2 && 5 < result) //better than flush
		{
			moneyBet = getMoney() * 0.3;
		}
		else
		{
			moneyBet = getMoney() * 0.4;
		}
		moneyBet *= 100;
		moneyBet = (int) moneyBet; //turn it into an int
		moneyBet /= 100; //back to double
		deduct(moneyBet);
		return moneyBet;
	}
	public void replaceCards(Deck d)
	{
		int result = GameRules.scoreCards(getCards());
		switch(result)
		{
		case 2:
			int pair = 0; //don't know what the pair is
			for(int i = 0; i < getCards().length; i++) //find the pair
			{
				if(getCards()[i].getFaceValue() == getCards()[i + 1].getFaceValue())
				{
					pair = getCards()[i].getFaceValue();
					break;
				}
			}
			for(int k = 0; k < getCards().length; k++) //go through and replace everything but
			{
				if(getCards()[k].getFaceValue() != pair) //replace it
				{
					d.returnToDeck(discard(k)); //return card to the deck
				}
			}
			Deck.fixCards(getCards());
			for(int g = 0; g < getCards().length; g++)
			{
				if(getCards()[g] == null)
				{
					getCard()[g] = d.deal(); //give them a new card
				}
			}
			break;
		case 3:
			int firstPair = 0, secondPair = 0;
			for(int i = 0; i < getCards().length - 1; i++) //find the pair
			{
				if(getCards()[i].getFaceValue() == getCards()[i + 1].getFaceValue())
				{
					if(firstPair == 0)
					{
						firstPair = getCards()[i].getFaceValue();
						i++;
					}
					else if(secondPair == 0)
					{
						secondPair = getCards()[i].getFaceValue();
						i++;
					}
				}
			}
			for(int b = 0; b < getCards().length; b++)
			{
				if(getCards()[b].getFaceValue() != secondPair && getCards()[b].getFaceValue() != firstPair)
				{
					d.returnToDeck(discard(b)); //return card to the deck
				}
			}
			Deck.fixCards(getCards());
			for(int g = 0; g < getCards().length; g++)
			{
				if(getCards()[g] == null)
				{
					getCard()[g] = d.deal(); //give them a new card
				}
			}
			break;
		case 4:
			int topNumber = 0;
			for(int i = 0; i < getCards().length - 1; i++) //find the pair
			{
				if(getCards()[i].getFaceValue() == getCards()[i + 1].getFaceValue())
				{
					topNumber = getCards()[i].getFaceValue();
				}
			}
			for(int k = 0; k < getCards().length; k++)
			{
				if(getCards()[k].getFaceValue() != topNumber) //replace it
				{
					d.returnToDeck(discard(k)); //return card to the deck
				}
			}
			Deck.fixCards(getCards());
			for(int g = 0; g < getCards().length; g++)
			{
				if(getCards()[g] == null)
				{
					getCard()[g] = d.deal(); //give them a new card
				}
			}			
			break;
		case 8: //just in case if the hand is more than five cards
			double one = 0, two = 0, three = 0, four = 0, five = 0, six = 0, seven = 0, eight = 0, nine = 0, ten = 0, jack = 0, queen = 0, king = 0, ace = 0;
			for(int i = 0; i < getCards().length; i++)
			{
				switch(getCards()[i].getFaceValue())
				{
				case 1:
					one++;
					break;
				case 2:
					two++;
					break;
				case 3:
					three++;
					break;
				case 4:
					four++;
					break;
				case 5:
					five++;
					break;
				case 6:
					six++;
					break;
				case 7:
					seven++;
					break;
				case 8:
					eight++;
					break;
				case 9:
					nine++;
					break;
				case 10:
					ten++;
					break;
				case 11:
					jack++;
					break;
				case 12:
					queen++;
					break;
				case 13:
					king++;
					break;
				case 14:
					ace++;
					break;
				}
			}
			int maxNumber = (int) Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(one, two), Math.max(three, four)), Math.max(Math.max(five, six), Math.max(seven, eight))), Math.max(nine, Math.max(ten, Math.max(jack, queen)))), king), ace);
			for(int i = 0; i < getCards().length - 1; i++)
			{
				if(getCards()[i].getFaceValue() != maxNumber)
				{
					d.returnToDeck(discard(i)); //return card to the deck
				}
			}
			Deck.fixCards(getCards());
			for(int g = 0; g < getCards().length; g++)
			{
				if(getCards()[g] == null)
				{
					getCard()[g] = d.deal(); //give them a new card
				}
			}
			break;
		case 1: //this is what happens if nothing is counted with the cards
			int royalflushCount = 0, flushCount = 0, straightCount = 0, straightflushCount = 0;
			for(int i = 0; i < getCards().length - 1; i++) //suits
			{
				if(getCards()[i].getFaceValue() == i + 10 && getCards()[i].getSuit().equals(getCards()[i + 1].getSuit()))
				{
					royalflushCount++;
				}
				if(getCards()[i].getFaceValue() + 1 == getCards()[i + 1].getFaceValue() && getCards()[i].getSuit().equals(getCards()[i + 1].getSuit()))
				{
					straightflushCount++;
				}
				if(getCards()[i].getFaceValue() + 1 == getCards()[i + 1].getFaceValue())
				{
					straightCount++;
				}
				if(getCards()[i].getSuit().equals(getCards()[i + 1].getSuit()))
				{
					flushCount++;
				}
			}
			int diamonds = 0, spades = 0, clubs = 0, hearts = 0, max = 0; //what is the suit 
			String majority = "";
			for(int i = 0; i < getCards().length - 1; i++) 
			{
				if(getCards()[i].getSuit().equals("Diamonds"))
				{
					diamonds++;
				}
				else if(getCards()[i].getSuit().equals("Spades"))
				{
					spades++;
				}
				else if(getCards()[i].getSuit().equals("Clubs"))
				{
					clubs++;
				}
				else
				{
					hearts++;
				}
			}
			max = Math.max(Math.max(diamonds, clubs), Math.max(hearts, spades)); //find the max
			if(max == diamonds)
			{
				majority = "Diamonds";
			}
			else if(max == clubs)
			{
				majority = "Clubs";
			}
			else if(max == spades)
			{
				majority = "Spades";
			}
			else if(max == hearts)
			{
				majority = "Hearts";
			}
			else
			{
				majority = "Null";
			}
			int top = Math.max(Math.max(royalflushCount, flushCount), Math.max(straightCount, straightflushCount));
			if(top == royalflushCount)
			{
				for(int b = 0; b < getCards().length - 1; b++)
				{
					if(getCards()[b].getFaceValue() != b + 10 || !getCards()[b].getSuit().equals(getCards()[b + 1].getSuit())) //if it doesn't work with suits or number replace it
					{
						d.returnToDeck(discard(b)); //return card to the deck
					}
				}
				Deck.fixCards(getCards());
				for(int g = 0; g < getCards().length; g++)
				{
					if(getCards()[g] == null)
					{
						getCard()[g] = d.deal(); //give them a new card
					}
				}
			}
			else if(top == flushCount)
			{
				for(int p = 0; p < getCards().length; p++)
				{
					if(!getCards()[p].getSuit().equals(majority))
					{
						d.returnToDeck(discard(p)); //return card to the deck
					}
				}
				Deck.fixCards(getCards());
				for(int g = 0; g < getCards().length; g++)
				{
					if(getCards()[g] == null)
					{
						getCard()[g] = d.deal(); //give them a new card
					}
				}
			}
			else if(top == straightCount)
			{
				int basedOff = 0;
				for(int b = 0; b < getCards().length - 1; b++)
				{
					if(getCards()[b].getFaceValue() - getCards()[b + 1].getFaceValue() == -1)
					{
						basedOff = getCards()[b].getFaceValue(); //what number will it be based off of
						break;
					}
				}
				for(int p = 0; p < getCards().length; p++)
				{
					if(getCards()[p].getFaceValue() - p * basedOff != -1)
					{
						d.returnToDeck(discard(p)); //return card to the deck
					}
				}
				Deck.fixCards(getCards());
				for(int g = 0; g < getCards().length; g++)
				{
					if(getCards()[g] == null)
					{
						getCard()[g] = d.deal(); //give them a new card
					}
				}
			}
			else if(top == straightflushCount)
			{
				int basedOff = 0;
				for(int b = 0; b < getCards().length - 1; b++)
				{
					if(getCards()[b].getFaceValue() - getCards()[b + 1].getFaceValue() == -1)
					{
						basedOff = getCards()[b].getFaceValue(); //what number will it be based off of
						break;
					}
				}
				for(int p = 0; p < getCards().length; p++)
				{
					if(getCards()[p].getFaceValue() - p * basedOff != -1 || !getCards()[p].getSuit().equals(majority))
					{
						d.returnToDeck(discard(p)); //return card to the deck
					}
				}
				Deck.fixCards(getCards());
				for(int g = 0; g < getCards().length; g++)
				{
					if(getCards()[g] == null)
					{
						getCard()[g] = d.deal(); //give them a new card
					}
				}
			}
		}
		Deck.sortCards(getCards());
	}
}
