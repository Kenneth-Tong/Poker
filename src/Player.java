import java.util.Arrays;

public class Player {
	private String name;
	private Card[] card;
	private int MAX_SIZE, currentSize, score;
	private boolean fold = false;
	public Player(String n, int max)
	{
		score = 0;
		name = n;
		MAX_SIZE = max;
		card = new Card[MAX_SIZE];
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public void setHand(Deck d)
	{
		for(int i = 0; i < MAX_SIZE; i++)
		{
			card[i] = d.deal();
		}
		Deck.sortCards(card);
		currentSize = MAX_SIZE;
	}
	public String showHand()
	{
		return Arrays.toString(card);
	}
	public Card discard(int i)
	{
		Card cardReturn = card[i];
		card[i] = null; //card reset = null
		return cardReturn;
	}
	public Card[] discard()
	{
		currentSize = 0;
		return card;
	}
	public void setCard(Card c)
	{
		card[currentSize] = c;
		currentSize++;
	}
	public void setCards(Card[] c)
	{
		card = c;
	}
	public int getMAX_SIZE() {
		return MAX_SIZE;
	}
	public void setMAX_SIZE(int mAX_SIZE) {
		MAX_SIZE = mAX_SIZE;
	}
	public int getCurrentSize() {
		return currentSize;
	}
	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}
	public void setCard(Card[] card) {
		this.card = card;
	}
	public Card[] getCards()
	{
		return card;
	}
	public boolean isFold() {
		return fold;
	}
	public void setFold(boolean fold) {
		this.fold = fold;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Card[] getCard() {
		return card;
	}
	public String toString()
	{
		return name;
	}
}