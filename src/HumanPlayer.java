public class HumanPlayer extends Player{
	private double money, raise = 0; //this determines how much they must pay extra
	private boolean bot;
	public double getRaise() { //raise determines how much they have already put in the pot
		return raise;
	}
	public void setRaise(double raise) {
		this.raise = raise;
	}
	public HumanPlayer(String n, int max)
	{
		super(n, max);
		money = 500;
		bot = false;
	}
	public boolean isBot() {
		return bot;
	}
	public void setBot(boolean bot) {
		this.bot = bot;
	}
	public boolean canCoverBet(double amt)
	{
		if(money - amt >= 0)
		{
			return true;
		}
		return false;
	}
	public void deduct(double amt)
	{
		money -= amt;
	}
	public void increase(double amt)
	{
		money += amt;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
}
