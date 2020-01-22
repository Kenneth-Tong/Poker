import java.util.Arrays;

public class Deck {
	private Card[] cards;
	private String[] suit = {"Clubs", "Diamonds", "Hearts", "Spades"};
	private String[] name = {"Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King", "Ace"};
	public static final int MAX_SIZE = 52;
	private int size;
	public Deck()
	{
		size = 52;
		cards = new Card[52];
		for(int i = 0; i < 4; i++)
		{
			for(int j = 2; j < 15; j++)
			{
				cards[i * 13 + j - 2] = new Card(suit[i], name[j - 2], j);
			}
		}
	}
	public Card deal()
	{
		Card topCard = cards[size - 1];
		cards[size - 1] = null;
		size--;
		return topCard;
	}
	public boolean returnToDeck(Card c)
	{
		if(size != MAX_SIZE)
		{
			for(int i = size; i > 0; i--)
			{
				cards[i] = cards[i - 1];
			}
			cards[0] = c;
			size++;
			return true;
		}
		return false;
	}
	public boolean returnToDeck(Card[] c)
	{
		for(int i = 0; i < c.length; i++)
		{
			if(c[i] != null)
			{
				boolean res = returnToDeck(c[i]);
				if(res == false)
				{
					return false;
				}
			}
		}
		return true;
	}
	public void shuffle()
	{
		for(int i = 0; i < size; i++)
		{
			int rand = (int) (Math.random() * size);
			Card card = cards[i];
			cards[i] = cards[rand];
			cards[rand] = card;
		}
	}
	public String toString()
	{
		return "Current size: " + (size) + "\nCards: " + Arrays.toString(cards);
	}
	/** deck helpers **/
	public static void sortCards(Card[] hand)
	{
		for(int i = 0; i < hand.length - 1; i++) //organize number
		{
			if(hand[i].getFaceValue() > hand[i + 1].getFaceValue())
			{
				Card replaceCard = hand[i];
				hand[i] = hand[i + 1];
				hand[i + 1] = replaceCard;
				i = -1; //reset it so it goes through the loop again
			}
		}
	}
	public static void fixCards(Card[] hand)
	{
		for(int i = 0; i < hand.length - 1; i++) //reset hand so null at the end
		{
			if(hand[i] == null)
			{
				hand[i] = hand[i + 1];
				hand[i + 1] = null;
			}
		}
	}		
}