public class Kitty {
	private double money;
	public Kitty()
	{
		money = 0;
	}
	public void update(double m)
	{
		money += m;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public double payout()
	{
		double moneyReturn = money;
		money = 0;
		return moneyReturn;
	}
}