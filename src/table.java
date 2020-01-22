import java.util.Arrays;
import java.util.Scanner;

public class table {
	private static Scanner scan = new Scanner(System.in);
	private static Deck deck = new Deck();
	private static int playerCount, gameRounds, currentRound, b = 0, dealer = 0, Human = 0, tiedWithHowMany = 0; //this keeps track of if everyone else folded and the dealer can fold and where in the list is the human player
	private static Player[] list;
	private static Kitty kitty = new Kitty();
	private static String win = "";
	private static boolean tie = false;
	private static Player[] tiedWith;
	private enum gameState
	{
		play, reveal, folded, lastBet;
	}
	private static gameState currentState = gameState.play;
	
	public static void main(String[] args) {
		int i = 1; //sets the round according to the loop
		currentRound = 1; //shows the round
		setUp();
		while(i <= gameRounds * 3)
		{
			switch(currentState)
			{
			case play:
				deck.shuffle(); //nothing wrong with making the deck shuffle twice
				System.out.println("--------Round " + currentRound + "--------");
				currentRound++; //next round will be 2 and so on
				System.out.println("The ante is: $" + GameRules.ante);
				scan.nextLine();
				for(int d = 0; d < playerCount; d++) //Put in money
				{
					if(((HumanPlayer)list[d]).getMoney() < GameRules.ante)
					{
						System.out.println(((HumanPlayer)list[d]).getName() + " doesn't have enough, they automatically fold.\n[Current money: $" + ((HumanPlayer)list[d]).getMoney() + "]");
						list[d].setFold(true);
						if(d == 0) //this is the dealer
						{
							dealer++;
						}
					}
					else
					{
						((HumanPlayer)list[d]).deduct(GameRules.ante);
						System.out.println(list[d].getName() + " puts in $" + GameRules.ante + "\n[Current value: $" + ((HumanPlayer)list[d]).getMoney() + "]");
						kitty.update(GameRules.ante);
					}
				}
				scan.nextLine();
				System.out.print("The Dealer is: " + list[dealer].getName());
				scan.nextLine();
				System.out.print(list[Human].getName() + ": Press enter to see your hand");
				scan.nextLine();
				list[Human].setHand(deck);
				Deck.sortCards(list[Human].getCards());
				System.out.println("Your hand: " + list[Human].showHand());
				for(int m = 0; m < playerCount; m++) //the bots player and human player if needed
				{
					if(!((HumanPlayer)list[m]).isFold()) //haven't folded
					{
						if(((HumanPlayer)list[m]).isBot())
						{
							playBot(((AIPlayer)list[m]));
						}
						else
						{
							play(((HumanPlayer)list[m]));
						}
					}
				}
				if(onePlayerLeft())
				{
					currentState = gameState.folded;
				}
				else
				{
					currentState = gameState.lastBet;
				}
				i++;
				break;
			case lastBet:
				System.out.println("\n------Last Betting Round------");
				bet(list[dealer]);
				currentState = gameState.reveal;
				i++;
				break;
			case reveal:
				System.out.println("\n------Reveal Cards------");
				scan.nextLine();
				for(int k = 0; k < playerCount; k++)
				{
					if(!list[k].isFold())
					{
						Deck.sortCards(list[k].getCards()); //sort their cards first
						list[k].setScore(GameRules.scoreCards(list[k].getCards()));
						System.out.println(list[k].getName() + ": " + list[k].showHand());
					}
				}
				winner(list); //set up whether there is a tie or not
				if(tie) //there is a tie
				{
					System.out.print("There is a tie between: " + Arrays.toString(tiedWith));
					double split = ((int) (kitty.getMoney() / (tiedWithHowMany + 1)) * 100) / 100; //make sure the percent is less than one hundreth
					for(int y = 0; y <= tiedWithHowMany; y++)
					{
						((HumanPlayer)tiedWith[y]).increase(split);
					}
					scan.nextLine();
					System.out.println("Each of them had a " + win);
					System.out.println("Each gets $" + split);
					for(int u = 0; u <= tiedWithHowMany; u++)
					{
						System.out.println(tiedWith[u].getName() + " now has $" + ((HumanPlayer)tiedWith[u]).getMoney());
					}
					kitty.setMoney(0); //no more money
				}
				else
				{
					((HumanPlayer)winner(list)).increase(kitty.payout()); //add their earnings from kitty and their value
					System.out.println(winner(list).getName() + " wins with a " + win + "!\n[" + winner(list).getName() + "'s total now: " + ((HumanPlayer)winner(list)).getMoney() + "]"); //Tell them who won
				}
					for(int o = 0; o < playerCount; o++)
				{
					deck.returnToDeck(list[o].discard()); //reset everything
					deck.shuffle(); //keep shuffling!
				}
				currentState = gameState.play;
				dealer = 0; //reset that the dealer hasn't folded
				rotateDealer();
				i++;
				tiedWith = new Player[playerCount]; //reset it
				tie = false;
				break;
			case folded:
				System.out.println("The winner is " + list[b].getName() + "!");
				System.out.println("[Everyone else folded]");
				scan.nextLine();
				((HumanPlayer)winner(list)).increase(kitty.payout());
				System.out.println(list[b].getName() + " had a " + win + "\nTheir hand: " + list[b].showHand() + "");
				System.out.println("Thier new sum of money: " + ((HumanPlayer)winner(list)).getMoney()); //get their money
				currentState = gameState.play;
				rotateDealer();
				dealer = 0; //reset that the dealer hasn't folded
				i++; //the rounds go up
				break;
			}
		}
		System.out.println("---------------Game Over---------------");
		Player currentWinner = list[0];
		for(int w = 1; w < playerCount - 1; w++)
		{
			if(((HumanPlayer)currentWinner).getMoney() < ((HumanPlayer)list[w]).getMoney())
			{
				currentWinner = (HumanPlayer)list[w];
			}
		}
		Player[] winners;
		int k = 0, winner = 1;
		winners = new Player[playerCount];
		winners[0] = currentWinner;
		while(k < playerCount) //see if ther are any ties
		{
			if(((HumanPlayer)currentWinner).getMoney() == ((HumanPlayer)list[k]).getMoney() && currentWinner != list[k])
			{
				winners[winner] = list[k];
				winner++; //amount of people that are in the tie
			}
			k++;
		}
		if(winner != 1) //there is a tie!
		{
			System.out.println("There is a tie between:" + Arrays.toString(winners));
			System.out.println("Each of them had $" + ((HumanPlayer)currentWinner).getMoney());
		}
		else
		{
			System.out.println("Winner: " + currentWinner.getName() + " with a total of $" + ((HumanPlayer)currentWinner).getMoney());
		}
		System.out.println("Thanks for playing!");
	}
	public static boolean onePlayerLeft()
	{
		int playersLeft = 0; //check every time if there is one person
		for(int k = 0; k < playerCount; k++) //one person is left who hasn't folded
		{
			if(!list[k].isFold())
			{
				playersLeft++;
			}
		}
		if(playersLeft == 1) //find the person if eveyrone else folded
		{
			for(b = 0; b < playerCount; b++)
			{
				if(!list[b].isFold()) //find the player to make sure
				{  
					return true;
				}
			}
		}
		return false;
	}
	public static void rotateDealer()
	{
		Player replace = list[0];
		for(int i = 0; i < playerCount - 1; i++)
		{
			list[i] = list[i + 1];
		}
		list[playerCount - 1] = replace;
		for(int b = 0; b < playerCount; b++) //everybody is in
		{
			list[b].setFold(false);
		}
		Human++; //player moves
		if(Human == playerCount) //keeps track of were the player is
		{
			Human = 0;
		}
	}
	public static void playBot(AIPlayer player)
	{
		System.out.println(player.getName() + ": Press enter to start your turn");
		timer();
		System.out.println("------------Menu------------\n1. View your cards and money\n2. Change a card\n3. End turn\n4. Help menu\n5. Raise the stakes [if you are the dealer]\n6. Fold\n[Select a number]");
		System.out.print("   >> ");
		timer();
		int foldOrNot = (int) (Math.random() * 1);
		if(foldOrNot == 0 && 1 == GameRules.scoreCards(player.getCards())) //low hand and feel like folding cause bots are not so bright people
		{
			System.out.println("3");
			System.out.println(player.getName() + " has ended their turn");
		}
		else
		{
			System.out.println("2");
			System.out.println("Cards have been changed!");
			player.replaceCards(deck);
		}
		if(player.getMoney() != 0 && list[dealer] == player) //sometimes it raises and other times not and if dealer, only the first bot gets to raise
		{
			System.out.println("------------Menu------------\n1. View your cards and money\n2. Change a card\n3. End turn\n4. Help menu\n5. Raise the stakes [if you are the dealer]\n6. Fold\n[Select a number]");
			System.out.print("   >> ");
			timer();
			bet(player);
		}
		System.out.println("------------Menu------------\n1. View your cards and money\n2. Change a card\n3. End turn\n4. Help menu\n5. Raise the stakes [if you are the dealer]\n6. Fold\n[Select a number]");
		System.out.print("   >> ");
		timer();
		System.out.println("3");
	}
	public static void play(HumanPlayer player)
	{
		System.out.print("\n" + player.getName() + ": Press enter to start your turn");
		scan.nextLine();
		Deck.sortCards(player.getCards());
		boolean turn = true, cardsChanged = false; //If they want to end
		int bet = 0; //you can only bet once during your turn if you're the dealer
		while(turn)
		{
			System.out.println("------------Menu------------\n1. View your cards and money\n2. Change a card\n3. End turn\n4. Help menu\n5. Raise the stakes [if you are the dealer]\n6. Fold\n[Select a number]");
			System.out.print("   >> ");
			int choice = scan.nextInt();
			scan.nextLine();
			switch(choice)
			{
			case 1:
				System.out.println("Your hand: " + Arrays.toString(player.getCard()) + "\nMoney: $" + player.getMoney());
				break;
			case 2:
				if(!cardsChanged)
				{
					System.out.println("Which cards would you like to replace? (Example: 1, 3, 5)\nYour hand: " + Arrays.toString(player.getCard()) + "\n[1 - 5, 0 to exit]");
					System.out.print("   >> ");
					String card = scan.nextLine();
					if(card.contains("0"))
					{
						break;
					}
					for(int k = 0; k < player.getCard().length; k++) //check if the card is in their hand
					{
						if(card.contains(Integer.toString(k + 1)))
						{
							deck.returnToDeck(player.discard(k)); //return card to the deck
						}
					}
					Deck.fixCards(player.getCards());
					for(int p = 0; p < player.getCards().length; p++)
					{
						if(player.getCards()[p] == null)
						{
							player.getCards()[p] = deck.deal();
						}
					}
					Deck.sortCards(player.getCards());
					System.out.println("New hand: " + Arrays.toString(player.getCard()));
					cardsChanged = true;
				}
				else
				{
					System.out.println("You already changed your cards!");
				}
				break;
			case 3:
				turn = false;
				break;
			case 4:
				System.out.println("Rules:\n1. You may only trade up to 5 cards during your turn\n2. You may not take more than 5 cards\n3. You must match the bet in the center or you fold");
				break;
			case 5:
				if(bet == 0) //if you've done both, you can't do this again
				{
					if(list[0] == player)
					{
						bet(player);
					}
					else
					{
						System.out.println("You are not the dealer!\n[Dealer is: " + list[0].getName() + "]");
					}
				}
				else
				{
					System.out.println("You can only bet once! (Later you can bet at the end)");
					scan.nextLine();
				}
				break;
			case 6:
				player.setFold(true);
				if(list[0] == player) //this is the dealer
				{
					dealer++;
				}
				turn = false;
				System.out.println(player.getName() + " has folded");
				break;
			default:
				System.out.println("That is not a choice on the menu.\n[Pick an number 1 - 5]");
				break;
			}
		}
	}
	public static void setUp()
	{
		System.out.println("---------------WELCOME TO THE CASINO!---------------\n[Press enter - This continues actions that aren't made by the bot]");
		scan.nextLine();
		System.out.println("This game runs by you, the player, pressing enter to continue\n[After a certian action occurs that isn't during hte bots turn, you will need to press space like right now]");
		scan.nextLine();
		System.out.println("How many bot players are playing tonight?\n[Bots perform their own turn, wait a second if it is their turn]");
		System.out.print("   >> ");
		playerCount = scan.nextInt();
		playerCount++; //it adds the player
		while(playerCount < 1)
		{
			System.out.println("How many bots are playing tonight?");
			System.out.print("   >> ");
			playerCount = scan.nextInt();
			scan.nextLine();
		}
		System.out.println("How many games are you playing for?");
		System.out.print("   >> ");
		gameRounds = scan.nextInt();
		scan.nextLine();
		while(gameRounds < 1)
		{
			System.out.println("How many games are you playing for?");
			System.out.print("   >> ");
			gameRounds = scan.nextInt();
			scan.nextLine();
		}
		list = new Player[playerCount];
		tiedWith = new Player[playerCount]; //this will keep track who's tied
		for(int i = 0; i < playerCount; i++)
		{
			String[] names = {"Aaran", "Aaren", "Aarez", "Aarman", "Aaron", "Aaron-James", "Aarron", "Aaryan", "Aaryn", "Aayan", "Aazaan", "Abaan", "Abbas", "Abdallah", "Abdalroof", "Abdihakim", "Abdirahman", "Abdisalam", "Abdul", "Abdul-Aziz", "Abdulbasir", "Abdulkadir", "Abdulkarem", "Abdulkhader", "Abdullah", "Abdul-Majeed", "Abdulmalik", "Abdul-Rehman", "Abdur", "Abdurraheem", "Abdur-Rahman", "Abdur-Rehmaan", "Abel", "Abhinav", "Abhisumant", "Abid", "Abir", "Abraham", "Abu", "Abubakar", "Ace", "Adain", "Adam", "Adam-James", "Addison", "Addisson", "Adegbola", "Adegbolahan", "Aden", "Adenn", "Adie", "Adil", "Aditya", "Adnan", "Adrian", "Adrien", "Aedan", "Aedin", "Aedyn", "Aeron", "Afonso", "Ahmad", "Ahmed", "Ahmed-Aziz", "Ahoua", "Ahtasham", "Aiadan", "Aidan", "Aiden", "Aiden-Jack", "Aiden-Vee", "Aidian", "Aidy", "Ailin", "Aiman", "Ainsley", "Ainslie", "Airen", "Airidas", "Airlie", "AJ", "Ajay", "A-Jay", "Ajayraj", "Akan", "Akram", "Al", "Ala", "Alan", "Alanas", "Alasdair", "Alastair", "Alber", "Albert", "Albie", "Aldred", "Alec", "Aled", "Aleem", "Aleksandar", "Aleksander", "Aleksandr", "Aleksandrs", "Alekzander", "Alessandro", "Alessio", "Alex", "Alexander", "Alexei", "Alexx", "Alexzander", "Alf", "Alfee", "Alfie", "Alfred", "Alfy", "Alhaji", "Al-Hassan", "Ali", "Aliekber", "Alieu", "Alihaider", "Alisdair", "Alishan", "Alistair", "Alistar", "Alister", "Aliyaan", "Allan", "Allan-Laiton", "Allen", "Allesandro", "Allister", "Ally", "Alphonse", "Altyiab", "Alum", "Alvern", "Alvin", "Alyas", "Amaan", "Aman", "Amani", "Ambanimoh", "Ameer", "Amgad", "Ami", "Amin", "Amir", "Ammaar", "Ammar", "Ammer", "Amolpreet", "Amos", "Amrinder", "Amrit", "Amro", "Anay", "Andrea", "Andreas", "Andrei", "Andrejs", "Andrew", "Andy", "Anees", "Anesu", "Angel", "Angelo", "Angus", "Anir", "Anis", "Anish", "Anmolpreet", "Annan", "Anndra", "Anselm", "Anthony", "Anthony-John", "Antoine", "Anton", "Antoni", "Antonio", "Antony", "Antonyo", "Anubhav", "Aodhan", "Aon", "Aonghus", "Apisai", "Arafat", "Aran", "Arandeep", "Arann", "Aray", "Arayan", "Archibald", "Archie", "Arda", "Ardal", "Ardeshir", "Areeb", "Areez", "Aref", "Arfin", "Argyle", "Argyll", "Ari", "Aria", "Arian", "Arihant", "Aristomenis", "Aristotelis", "Arjuna", "Arlo", "Armaan", "Arman", "Armen", "Arnab", "Arnav", "Arnold", "Aron", "Aronas", "Arran", "Arrham", "Arron", "Arryn", "Arsalan", "Artem", "Arthur", "Artur", "Arturo", "Arun", "Arunas", "Arved", "Arya", "Aryan", "Aryankhan", "Aryian", "Aryn", "Asa", "Asfhan", "Ash", "Ashlee-jay", "Ashley", "Ashton", "Ashton-Lloyd", "Ashtyn", "Ashwin", "Asif", "Asim", "Aslam", "Asrar", "Ata", "Atal", "Atapattu", "Ateeq", "Athol", "Athon", "Athos-Carlos", "Atli", "Atom", "Attila", "Aulay", "Aun", "Austen", "Austin", "Avani", "Averon", "Avi", "Avinash", "Avraham", "Awais", "Awwal", "Axel", "Ayaan", "Ayan", "Aydan", "Ayden", "Aydin", "Aydon", "Ayman", "Ayomide", "Ayren", "Ayrton", "Aytug", "Ayub", "Ayyub", "Azaan", "Azedine", "Azeem", "Azim", "Aziz", "Azlan", "Azzam", "Azzedine", "Babatunmise", "Babur", "Bader", "Badr", "Badsha", "Bailee", "Bailey", "Bailie", "Bailley", "Baillie", "Baley", "Balian", "Banan", "Barath", "Barkley", "Barney", "Baron", "Barrie", "Barry", "Bartlomiej", "Bartosz", "Basher", "Basile", "Baxter", "Baye", "Bayley", "Beau", "Beinn", "Bekim", "Believe", "Ben", "Bendeguz", "Benedict", "Benjamin", "Benjamyn", "Benji", "Benn", "Bennett", "Benny", "Benoit", "Bentley", "Berkay", "Bernard", "Bertie", "Bevin", "Bezalel", "Bhaaldeen", "Bharath", "Bilal", "Bill", "Billy", "Binod", "Bjorn", "Blaike", "Blaine", "Blair", "Blaire", "Blake", "Blazej", "Blazey", "Blessing", "Blue", "Blyth", "Bo", "Boab", "Bob", "Bobby", "Bobby-Lee", "Bodhan", "Boedyn", "Bogdan", "Bohbi", "Bony", "Bowen", "Bowie", "Boyd", "Bracken", "Brad", "Bradan", "Braden", "Bradley", "Bradlie", "Bradly", "Brady", "Bradyn", "Braeden", "Braiden", "Brajan", "Brandan", "Branden", "Brandon", "Brandonlee", "Brandon-Lee", "Brandyn", "Brannan", "Brayden", "Braydon", "Braydyn", "Breandan", "Brehme", "Brendan", "Brendon", "Brendyn", "Breogan", "Bret", "Brett", "Briaddon", "Brian", "Brodi", "Brodie", "Brody", "Brogan", "Broghan", "Brooke", "Brooklin", "Brooklyn", "Bruce", "Bruin", "Bruno", "Brunon", "Bryan", "Bryce", "Bryden", "Brydon", "Brydon-Craig", "Bryn", "Brynmor", "Bryson", "Buddy", "Bully", "Burak", "Burhan", "Butali", "Butchi", "Byron", "Cabhan", "Cadan", "Cade", "Caden", "Cadon", "Cadyn", "Caedan", "Caedyn", "Cael", "Caelan", "Caelen", "Caethan", "Cahl", "Cahlum", "Cai", "Caidan", "Caiden", "Caiden-Paul", "Caidyn", "Caie", "Cailaen", "Cailean", "Caileb-John", "Cailin", "Cain", "Caine", "Cairn", "Cal", "Calan", "Calder", "Cale", "Calean", "Caleb", "Calen", "Caley", "Calib", "Calin", "Callahan", "Callan", "Callan-Adam", "Calley", "Callie", "Callin", "Callum", "Callun", "Callyn", "Calum", "Calum-James", "Calvin", "Cambell", "Camerin", "Cameron", "Campbel", "Campbell", "Camron", "Caolain", "Caolan", "Carl", "Carlo", "Carlos", "Carrich", "Carrick", "Carson", "Carter", "Carwyn", "Casey", "Casper", "Cassy", "Cathal", "Cator", "Cavan", "Cayden", "Cayden-Robert", "Cayden-Tiamo", "Ceejay", "Ceilan", "Ceiran", "Ceirin", "Ceiron", "Cejay", "Celik", "Cephas", "Cesar", "Cesare", "Chad", "Chaitanya", "Chang-Ha", "Charles", "Charley", "Charlie", "Charly", "Chase", "Che", "Chester", "Chevy", "Chi", "Chibudom", "Chidera", "Chimsom", "Chin", "Chintu", "Chiqal", "Chiron", "Chris", "Chris-Daniel", "Chrismedi", "Christian", "Christie", "Christoph", "Christopher", "Christopher-Lee", "Christy", "Chu", "Chukwuemeka", "Cian", "Ciann", "Ciar", "Ciaran", "Ciarian", "Cieran", "Cillian", "Cillin", "Cinar", "CJ", "C-Jay", "Clark", "Clarke", "Clayton", "Clement", "Clifford", "Clyde", "Cobain", "Coban", "Coben", "Cobi", "Cobie", "Coby", "Codey", "Codi", "Codie", "Cody", "Cody-Lee", "Coel", "Cohan", "Cohen", "Colby", "Cole", "Colin", "Coll", "Colm", "Colt", "Colton", "Colum", "Colvin", "Comghan", "Conal", "Conall", "Conan", "Conar", "Conghaile", "Conlan", "Conley", "Conli", "Conlin", "Conlly", "Conlon", "Conlyn", "Connal", "Connall", "Connan", "Connar", "Connel", "Connell", "Conner", "Connolly", "Connor", "Connor-David", "Conor", "Conrad", "Cooper", "Copeland", "Coray", "Corben", "Corbin", "Corey", "Corey-James", "Corey-Jay", "Cori", "Corie", "Corin", "Cormac", "Cormack", "Cormak", "Corran", "Corrie", "Cory", "Cosmo", "Coupar", "Craig", "Craig-James", "Crawford", "Creag", "Crispin", "Cristian", "Crombie", "Cruiz", "Cruz", "Cuillin", "Cullen", "Cullin", "Curtis", "Cyrus", "Daanyaal", "Daegan", "Daegyu", "Dafydd", "Dagon", "Dailey", "Daimhin", "Daithi", "Dakota", "Daksh", "Dale", "Dalong", "Dalton", "Damian", "Damien", "Damon", "Dan", "Danar", "Dane", "Danial", "Daniel", "Daniele", "Daniel-James", "Daniels", "Daniil", "Danish", "Daniyal", "Danniel", "Danny", "Dante", "Danyal", "Danyil", "Danys", "Daood", "Dara", "Darach", "Daragh", "Darcy", "D'arcy", "Dareh", "Daren", "Darien", "Darius", "Darl", "Darn", "Darrach", "Darragh", "Darrel", "Darrell", "Darren", "Darrie", "Darrius", "Darroch", "Darryl", "Darryn", "Darwyn", "Daryl", "Daryn", "Daud", "Daumantas", "Davi", "David", "David-Jay", "David-Lee", "Davie", "Davis", "Davy", "Dawid", "Dawson", "Dawud", "Dayem", "Daymian", "Deacon", "Deagan", "Dean", "Deano", "Decklan", "Declain", "Declan", "Declyan", "Declyn", "Dedeniseoluwa", "Deecan", "Deegan", "Deelan", "Deklain-Jaimes", "Del", "Demetrius", "Denis", "Deniss", "Dennan", "Dennin", "Dennis", "Denny", "Dennys", "Denon", "Denton", "Denver", "Denzel", "Deon", "Derek", "Derick", "Derin", "Dermot", "Derren", "Derrie", "Derrin", "Derron", "Derry", "Derryn", "Deryn", "Deshawn", "Desmond", "Dev", "Devan", "Devin", "Devlin", "Devlyn", "Devon", "Devrin", "Devyn", "Dex", "Dexter", "Dhani", "Dharam", "Dhavid", "Dhyia", "Diarmaid", "Diarmid", "Diarmuid", "Didier", "Diego", "Diesel", "Diesil", "Digby", "Dilan", "Dilano", "Dillan", "Dillon", "Dilraj", "Dimitri", "Dinaras", "Dion", "Dissanayake", "Dmitri", "Doire", "Dolan", "Domanic", "Domenico", "Domhnall", "Dominic", "Dominick", "Dominik", "Donald", "Donnacha", "Donnie", "Dorian", "Dougal", "Douglas", "Dougray", "Drakeo", "Dre", "Dregan", "Drew", "Dugald", "Duncan", "Duriel", "Dustin", "Dylan", "Dylan-Jack", "Dylan-James", "Dylan-John", "Dylan-Patrick", "Dylin", "Dyllan", "Dyllan-James", "Dyllon", "Eadie", "Eagann", "Eamon", "Eamonn", "Eason", "Eassan", "Easton", "Ebow", "Ed", "Eddie", "Eden", "Ediomi", "Edison", "Eduardo", "Eduards", "Edward", "Edwin", "Edwyn", "Eesa", "Efan", "Efe", "Ege", "Ehsan", "Ehsen", "Eiddon", "Eidhan", "Eihli", "Eimantas", "Eisa", "Eli", "Elias", "Elijah", "Eliot", "Elisau", "Eljay", "Eljon", "Elliot", "Elliott", "Ellis", "Ellisandro", "Elshan", "Elvin", "Elyan", "Emanuel", "Emerson", "Emil", "Emile", "Emir", "Emlyn", "Emmanuel", "Emmet", "Eng", "Eniola", "Enis", "Ennis", "Enrico", "Enrique", "Enzo", "Eoghain", "Eoghan", "Eoin", "Eonan", "Erdehan", "Eren", "Erencem", "Eric", "Ericlee", "Erik", "Eriz", "Ernie-Jacks", "Eroni", "Eryk", "Eshan", "Essa", "Esteban", "Ethan", "Etienne", "Etinosa", "Euan", "Eugene", "Evan", "Evann", "Ewan", "Ewen", "Ewing", "Exodi", "Ezekiel", "Ezra", "Fabian", "Fahad", "Faheem", "Faisal", "Faizaan", "Famara", "Fares", "Farhaan", "Farhan", "Farren", "Farzad", "Fauzaan", "Favour", "Fawaz", "Fawkes", "Faysal", "Fearghus", "Feden", "Felix", "Fergal", "Fergie", "Fergus", "Ferre", "Fezaan", "Fiachra", "Fikret", "Filip", "Filippo", "Finan", "Findlay", "Findlay-James", "Findlie", "Finlay", "Finley", "Finn", "Finnan", "Finnean", "Finnen", "Finnlay", "Finnley", "Fintan", "Fionn", "Firaaz", "Fletcher", "Flint", "Florin", "Flyn", "Flynn", "Fodeba", "Folarinwa", "Forbes", "Forgan", "Forrest", "Fox", "Francesco", "Francis", "Francisco", "Franciszek", "Franco", "Frank", "Frankie", "Franklin", "Franko", "Fraser", "Frazer", "Fred", "Freddie", "Frederick", "Fruin", "Fyfe", "Fyn", "Fynlay", "Fynn", "Gabriel", "Gallagher", "Gareth", "Garren", "Garrett", "Garry", "Gary", "Gavin", "Gavin-Lee", "Gene", "Geoff", "Geoffrey", "Geomer", "Geordan", "Geordie", "George", "Georgia", "Georgy", "Gerard", "Ghyll", "Giacomo", "Gian", "Giancarlo", "Gianluca", "Gianmarco", "Gideon", "Gil", "Gio", "Girijan", "Girius", "Gjan", "Glascott", "Glen", "Glenn", "Gordon", "Grady", "Graeme", "Graham", "Grahame", "Grant", "Grayson", "Greg", "Gregor", "Gregory", "Greig", "Griffin", "Griffyn", "Grzegorz", "Guang", "Guerin", "Guillaume", "Gurardass", "Gurdeep", "Gursees", "Gurthar", "Gurveer", "Gurwinder", "Gus", "Gustav", "Guthrie", "Guy", "Gytis", "Habeeb", "Hadji", "Hadyn", "Hagun", "Haiden", "Haider", "Hamad", "Hamid", "Hamish", "Hamza", "Hamzah", "Han", "Hansen", "Hao", "Hareem", "Hari", "Harikrishna", "Haris", "Harish", "Harjeevan", "Harjyot", "Harlee", "Harleigh", "Harley", "Harman", "Harnek", "Harold", "Haroon", "Harper", "Harri", "Harrington", "Harris", "Harrison", "Harry", "Harvey", "Harvie", "Harvinder", "Hasan", "Haseeb", "Hashem", "Hashim", "Hassan", "Hassanali", "Hately", "Havila", "Hayden", "Haydn", "Haydon", "Haydyn", "Hcen", "Hector", "Heddle", "Heidar", "Heini", "Hendri", "Henri", "Henry", "Herbert", "Heyden", "Hiro", "Hirvaansh", "Hishaam", "Hogan", "Honey", "Hong", "Hope", "Hopkin", "Hosea", "Howard", "Howie", "Hristomir", "Hubert", "Hugh", "Hugo", "Humza", "Hunter", "Husnain", "Hussain", "Hussan", "Hussnain", "Hussnan", "Hyden", "I", "Iagan", "Iain", "Ian", "Ibraheem", "Ibrahim", "Idahosa", "Idrees", "Idris", "Iestyn", "Ieuan", "Igor", "Ihtisham", "Ijay", "Ikechukwu", "Ikemsinachukwu", "Ilyaas", "Ilyas", "Iman", "Immanuel", "Inan", "Indy", "Ines", "Innes", "Ioannis", "Ireayomide", "Ireoluwa", "Irvin", "Irvine", "Isa", "Isaa", "Isaac", "Isaiah", "Isak", "Isher", "Ishwar", "Isimeli", "Isira", "Ismaeel", "Ismail", "Israel", "Issiaka", "Ivan", "Ivar", "Izaak", "J", "Jaay", "Jac", "Jace", "Jack", "Jacki", "Jackie", "Jack-James", "Jackson", "Jacky", "Jacob", "Jacques", "Jad", "Jaden", "Jadon", "Jadyn", "Jae", "Jagat", "Jago", "Jaheim", "Jahid", "Jahy", "Jai", "Jaida", "Jaiden", "Jaidyn", "Jaii", "Jaime", "Jai-Rajaram", "Jaise", "Jak", "Jake", "Jakey", "Jakob", "Jaksyn", "Jakub", "Jamaal", "Jamal", "Jameel", "Jameil", "James", "James-Paul", "Jamey", "Jamie", "Jan", "Jaosha", "Jardine", "Jared", "Jarell", "Jarl", "Jarno", "Jarred", "Jarvi", "Jasey-Jay", "Jasim", "Jaskaran", "Jason", "Jasper", "Jaxon", "Jaxson", "Jay", "Jaydan", "Jayden", "Jayden-James", "Jayden-Lee", "Jayden-Paul", "Jayden-Thomas", "Jaydn", "Jaydon", "Jaydyn", "Jayhan", "Jay-Jay", "Jayke", "Jaymie", "Jayse", "Jayson", "Jaz", "Jazeb", "Jazib", "Jazz", "Jean", "Jean-Lewis", "Jean-Pierre", "Jebadiah", "Jed", "Jedd", "Jedidiah", "Jeemie", "Jeevan", "Jeffrey", "Jensen", "Jenson", "Jensyn", "Jeremy", "Jerome", "Jeronimo", "Jerrick", "Jerry", "Jesse", "Jesuseun", "Jeswin", "Jevan", "Jeyun", "Jez", "Jia", "Jian", "Jiao", "Jimmy", "Jincheng", "JJ", "Joaquin", "Joash", "Jock", "Jody", "Joe", "Joeddy", "Joel", "Joey", "Joey-Jack", "Johann", "Johannes", "Johansson", "John", "Johnathan", "Johndean", "Johnjay", "John-Michael", "Johnnie", "Johnny", "Johnpaul", "John-Paul", "John-Scott", "Johnson", "Jole", "Jomuel", "Jon", "Jonah", "Jonatan", "Jonathan", "Jonathon", "Jonny", "Jonothan", "Jon-Paul", "Jonson", "Joojo", "Jordan", "Jordi", "Jordon", "Jordy", "Jordyn", "Jorge", "Joris", "Jorryn", "Josan", "Josef", "Joseph", "Josese", "Josh", "Joshiah", "Joshua", "Josiah", "Joss", "Jostelle", "Joynul", "Juan", "Jubin", "Judah", "Jude", "Jules", "Julian", "Julien", "Jun", "Junior", "Jura", "Justan", "Justin", "Justinas", "Kaan", "Kabeer", "Kabir", "Kacey", "Kacper", "Kade", "Kaden", "Kadin", "Kadyn", "Kaeden", "Kael", "Kaelan", "Kaelin", "Kaelum", "Kai", "Kaid", "Kaidan", "Kaiden", "Kaidinn", "Kaidyn", "Kaileb", "Kailin", "Kain", "Kaine", "Kainin", "Kainui", "Kairn", "Kaison", "Kaiwen", "Kajally", "Kajetan", "Kalani", "Kale", "Kaleb", "Kaleem", "Kal-el", "Kalen", "Kalin", "Kallan", "Kallin", "Kalum", "Kalvin", "Kalvyn", "Kameron", "Kames", "Kamil", "Kamran", "Kamron", "Kane", "Karam", "Karamvir", "Karandeep", "Kareem", "Karim", "Karimas", "Karl", "Karol", "Karson", "Karsyn", "Karthikeya", "Kasey", "Kash", "Kashif", "Kasim", "Kasper", "Kasra", "Kavin", "Kayam", "Kaydan", "Kayden", "Kaydin", "Kaydn", "Kaydyn", "Kaydyne", "Kayleb", "Kaylem", "Kaylum", "Kayne", "Kaywan", "Kealan", "Kealon", "Kean", "Keane", "Kearney", "Keatin", "Keaton", "Keavan", "Keayn", "Kedrick", "Keegan", "Keelan", "Keelin", "Keeman", "Keenan", "Keenan-Lee", "Keeton", "Kehinde", "Keigan", "Keilan", "Keir", "Keiran", "Keiren", "Keiron", "Keiryn", "Keison", "Keith", "Keivlin", "Kelam", "Kelan", "Kellan", "Kellen", "Kelso", "Kelum", "Kelvan", "Kelvin", "Ken", "Kenan", "Kendall", "Kendyn", "Kenlin", "Kenneth", "Kensey", "Kenton", "Kenyon", "Kenzeigh", "Kenzi", "Kenzie", "Kenzo", "Kenzy", "Keo", "Ker", "Kern", "Kerr", "Kevan", "Kevin", "Kevyn", "Kez", "Khai", "Khalan", "Khaleel", "Khaya", "Khevien", "Khizar", "Khizer", "Kia", "Kian", "Kian-James", "Kiaran", "Kiarash", "Kie", "Kiefer", "Kiegan", "Kienan", "Kier", "Kieran", "Kieran-Scott", "Kieren", "Kierin", "Kiern", "Kieron", "Kieryn", "Kile", "Killian", "Kimi", "Kingston", "Kinneil", "Kinnon", "Kinsey", "Kiran", "Kirk", "Kirwin", "Kit", "Kiya", "Kiyonari", "Kjae", "Klein", "Klevis", "Kobe", "Kobi", "Koby", "Koddi", "Koden", "Kodi", "Kodie", "Kody", "Kofi", "Kogan", "Kohen", "Kole", "Konan", "Konar", "Konnor", "Konrad", "Koray", "Korben", "Korbyn", "Korey", "Kori", "Korrin", "Kory", "Koushik", "Kris", "Krish", "Krishan", "Kriss", "Kristian", "Kristin", "Kristofer", "Kristoffer", "Kristopher", "Kruz", "Krzysiek", "Krzysztof", "Ksawery", "Ksawier", "Kuba", "Kurt", "Kurtis", "Kurtis-Jae", "Kyaan", "Kyan", "Kyde", "Kyden", "Kye", "Kyel", "Kyhran", "Kyie", "Kylan", "Kylar", "Kyle", "Kyle-Derek", "Kylian", "Kym", "Kynan", "Kyral", "Kyran", "Kyren", "Kyrillos", "Kyro", "Kyron", "Kyrran", "Lachlainn", "Lachlan", "Lachlann", "Lael", "Lagan", "Laird", "Laison", "Lakshya", "Lance", "Lancelot", "Landon", "Lang", "Lasse", "Latif", "Lauchlan", "Lauchlin", "Laughlan", "Lauren", "Laurence", "Laurie", "Lawlyn", "Lawrence", "Lawrie", "Lawson", "Layne", "Layton", "Lee", "Leigh", "Leigham", "Leighton", "Leilan", "Leiten", "Leithen", "Leland", "Lenin", "Lennan", "Lennen", "Lennex", "Lennon", "Lennox", "Lenny", "Leno", "Lenon", "Lenyn", "Leo", "Leon", "Leonard", "Leonardas", "Leonardo", "Lepeng", "Leroy", "Leven", "Levi", "Levon", "Levy", "Lewie", "Lewin", "Lewis", "Lex", "Leydon", "Leyland", "Leylann", "Leyton", "Liall", "Liam", "Liam-Stephen", "Limo", "Lincoln", "Lincoln-John", "Lincon", "Linden", "Linton", "Lionel", "Lisandro", "Litrell", "Liyonela-Elam", "LLeyton", "Lliam", "Lloyd", "Lloyde", "Loche", "Lochlan", "Lochlann", "Lochlan-Oliver", "Lock", "Lockey", "Logan", "Logann", "Logan-Rhys", "Loghan", "Lokesh", "Loki", "Lomond", "Lorcan", "Lorenz", "Lorenzo", "Lorne", "Loudon", "Loui", "Louie", "Louis", "Loukas", "Lovell", "Luc", "Luca", "Lucais", "Lucas", "Lucca", "Lucian", "Luciano", "Lucien", "Lucus", "Luic", "Luis", "Luk", "Luka", "Lukas", "Lukasz", "Luke", "Lukmaan", "Luqman", "Lyall", "Lyle", "Lyndsay", "Lysander", "Maanav", "Maaz", "Mac", "Macallum", "Macaulay", "Macauley", "Macaully", "Machlan", "Maciej", "Mack", "Mackenzie", "Mackenzy", "Mackie", "Macsen", "Macy", "Madaki", "Maddison", "Maddox", "Madison", "Madison-Jake", "Madox", "Mael", "Magnus", "Mahan", "Mahdi", "Mahmoud", "Maias", "Maison", "Maisum", "Maitlind", "Majid", "Makensie", "Makenzie", "Makin", "Maksim", "Maksymilian", "Malachai", "Malachi", "Malachy", "Malakai", "Malakhy", "Malcolm", "Malik", "Malikye", "Malo", "Ma'moon", "Manas", "Maneet", "Manmohan", "Manolo", "Manson", "Mantej", "Manuel", "Manus", "Marc", "Marc-Anthony", "Marcel", "Marcello", "Marcin", "Marco", "Marcos", "Marcous", "Marcquis", "Marcus", "Mario", "Marios", "Marius", "Mark", "Marko", "Markus", "Marley", "Marlin", "Marlon", "Maros", "Marshall", "Martin", "Marty", "Martyn", "Marvellous", "Marvin", "Marwan", "Maryk", "Marzuq", "Mashhood", "Mason", "Mason-Jay", "Masood", "Masson", "Matas", "Matej", "Mateusz", "Mathew", "Mathias", "Mathu", "Mathuyan", "Mati", "Matt", "Matteo", "Matthew", "Matthew-William", "Matthias", "Max", "Maxim", "Maximilian", "Maximillian", "Maximus", "Maxwell", "Maxx", "Mayeul", "Mayson", "Mazin", "Mcbride", "McCaulley", "McKade", "McKauley", "McKay", "McKenzie", "McLay", "Meftah", "Mehmet", "Mehraz", "Meko", "Melville", "Meshach", "Meyzhward", "Micah", "Michael", "Michael-Alexander", "Michael-James", "Michal", "Michat", "Micheal", "Michee", "Mickey", "Miguel", "Mika", "Mikael", "Mikee", "Mikey", "Mikhail", "Mikolaj", "Miles", "Millar", "Miller", "Milo", "Milos", "Milosz", "Mir", "Mirza", "Mitch", "Mitchel", "Mitchell", "Moad", "Moayd", "Mobeen", "Modoulamin", "Modu", "Mohamad", "Mohamed", "Mohammad", "Mohammad-Bilal", "Mohammed", "Mohanad", "Mohd", "Momin", "Momooreoluwa", "Montague", "Montgomery", "Monty", "Moore", "Moosa", "Moray", "Morgan", "Morgyn", "Morris", "Morton", "Moshy", "Motade", "Moyes", "Msughter", "Mueez", "Muhamadjavad", "Muhammad", "Muhammed", "Muhsin", "Muir", "Munachi", "Muneeb", "Mungo", "Munir", "Munmair", "Munro", "Murdo", "Murray", "Murrough", "Murry", "Musa", "Musse", "Mustafa", "Mustapha", "Muzammil", "Muzzammil", "Mykie", "Myles", "Mylo", "Nabeel", "Nadeem", "Nader", "Nagib", "Naif", "Nairn", "Narvic", "Nash", "Nasser", "Nassir", "Natan", "Nate", "Nathan", "Nathanael", "Nathanial", "Nathaniel", "Nathan-Rae", "Nawfal", "Nayan", "Neco", "Neil", "Nelson", "Neo", "Neshawn", "Nevan", "Nevin", "Ngonidzashe", "Nial", "Niall", "Nicholas", "Nick", "Nickhill", "Nicki", "Nickson", "Nicky", "Nico", "Nicodemus", "Nicol", "Nicolae", "Nicolas", "Nidhish", "Nihaal", "Nihal", "Nikash", "Nikhil", "Niki", "Nikita", "Nikodem", "Nikolai", "Nikos", "Nilav", "Niraj", "Niro", "Niven", "Noah", "Noel", "Nolan", "Noor", "Norman", "Norrie", "Nuada", "Nyah", "Oakley", "Oban", "Obieluem", "Obosa", "Odhran", "Odin", "Odynn", "Ogheneochuko", "Ogheneruno", "Ohran", "Oilibhear", "Oisin", "Ojima-Ojo", "Okeoghene", "Olaf", "Ola-Oluwa", "Olaoluwapolorimi", "Ole", "Olie", "Oliver", "Olivier", "Oliwier", "Ollie", "Olurotimi", "Oluwadamilare", "Oluwadamiloju", "Oluwafemi", "Oluwafikunayomi", "Oluwalayomi", "Oluwatobiloba", "Oluwatoni", "Omar", "Omri", "Oran", "Orin", "Orlando", "Orley", "Orran", "Orrick", "Orrin", "Orson", "Oryn", "Oscar", "Osesenagha", "Oskar", "Ossian", "Oswald", "Otto", "Owain", "Owais", "Owen", "Owyn", "Oz", "Ozzy", "Pablo", "Pacey", "Padraig", "Paolo", "Pardeepraj", "Parkash", "Parker", "Pascoe", "Pasquale", "Patrick", "Patrick-John", "Patrikas", "Patryk", "Paul", "Pavit", "Pawel", "Pawlo", "Pearce", "Pearse", "Pearsen", "Pedram", "Pedro", "Peirce", "Peiyan", "Pele", "Peni", "Peregrine", "Peter", "Phani", "Philip", "Philippos", "Phinehas", "Phoenix", "Phoevos", "Pierce", "Pierre-Antoine", "Pieter", "Pietro", "Piotr", "Porter", "Prabhjoit", "Prabodhan", "Praise", "Pranav", "Pravin", "Precious", "Prentice", "Presley", "Preston", "Preston-Jay", "Prinay", "Prince", "Prithvi", "Promise", "Puneetpaul", "Pushkar", "Qasim", "Qirui", "Quinlan", "Quinn", "Radmiras", "Raees", "Raegan", "Rafael", "Rafal", "Rafferty", "Rafi", "Raheem", "Rahil", "Rahim", "Rahman", "Raith", "Raithin", "Raja", "Rajab-Ali", "Rajan", "Ralfs", "Ralph", "Ramanas", "Ramit", "Ramone", "Ramsay", "Ramsey", "Rana", "Ranolph", "Raphael", "Rasmus", "Rasul", "Raul", "Raunaq", "Ravin", "Ray", "Rayaan", "Rayan", "Rayane", "Rayden", "Rayhan", "Raymond", "Rayne", "Rayyan", "Raza", "Reace", "Reagan", "Reean", "Reece", "Reed", "Reegan", "Rees", "Reese", "Reeve", "Regan", "Regean", "Reggie", "Rehaan", "Rehan", "Reice", "Reid", "Reigan", "Reilly", "Reily", "Reis", "Reiss", "Remigiusz", "Remo", "Remy", "Ren", "Renars", "Reng", "Rennie", "Reno", "Reo", "Reuben", "Rexford", "Reynold", "Rhein", "Rheo", "Rhett", "Rheyden", "Rhian", "Rhoan", "Rholmark", "Rhoridh", "Rhuairidh", "Rhuan", "Rhuaridh", "Rhudi", "Rhy", "Rhyan", "Rhyley", "Rhyon", "Rhys", "Rhys-Bernard", "Rhyse", "Riach", "Rian", "Ricards", "Riccardo", "Ricco", "Rice", "Richard", "Richey", "Richie", "Ricky", "Rico", "Ridley", "Ridwan", "Rihab", "Rihan", "Rihards", "Rihonn", "Rikki", "Riley", "Rio", "Rioden", "Rishi", "Ritchie", "Rivan", "Riyadh", "Riyaj", "Roan", "Roark", "Roary", "Rob", "Robbi", "Robbie", "Robbie-lee", "Robby", "Robert", "Robert-Gordon", "Robertjohn", "Robi", "Robin", "Rocco", "Roddy", "Roderick", "Rodrigo", "Roen", "Rogan", "Roger", "Rohaan", "Rohan", "Rohin", "Rohit", "Rokas", "Roman", "Ronald", "Ronan", "Ronan-Benedict", "Ronin", "Ronnie", "Rooke", "Roray", "Rori", "Rorie", "Rory", "Roshan", "Ross", "Ross-Andrew", "Rossi", "Rowan", "Rowen", "Roy", "Ruadhan", "Ruaidhri", "Ruairi", "Ruairidh", "Ruan", "Ruaraidh", "Ruari", "Ruaridh", "Ruben", "Rubhan", "Rubin", "Rubyn", "Rudi", "Rudy", "Rufus", "Rui", "Ruo", "Rupert", "Ruslan", "Russel", "Russell", "Ryaan", "Ryan", "Ryan-Lee", "Ryden", "Ryder", "Ryese", "Ryhs", "Rylan", "Rylay", "Rylee", "Ryleigh", "Ryley", "Rylie", "Ryo", "Ryszard", "Saad", "Sabeen", "Sachkirat", "Saffi", "Saghun", "Sahaib", "Sahbian", "Sahil", "Saif", "Saifaddine", "Saim", "Sajid", "Sajjad", "Salahudin", "Salman", "Salter", "Salvador", "Sam", "Saman", "Samar", "Samarjit", "Samatar", "Sambrid", "Sameer", "Sami", "Samir", "Sami-Ullah", "Samual", "Samuel", "Samuela", "Samy", "Sanaullah", "Sandro", "Sandy", "Sanfur", "Sanjay", "Santiago", "Santino", "Satveer", "Saul", "Saunders", "Savin", "Sayad", "Sayeed", "Sayf", "Scot", "Scott", "Scott-Alexander", "Seaan", "Seamas", "Seamus", "Sean", "Seane", "Sean-James", "Sean-Paul", "Sean-Ray", "Seb", "Sebastian", "Sebastien", "Selasi", "Seonaidh", "Sephiroth", "Sergei", "Sergio", "Seth", "Sethu", "Seumas", "Shaarvin", "Shadow", "Shae", "Shahmir", "Shai", "Shane", "Shannon", "Sharland", "Sharoz", "Shaughn", "Shaun", "Shaunpaul", "Shaun-Paul", "Shaun-Thomas", "Shaurya", "Shaw", "Shawn", "Shawnpaul", "Shay", "Shayaan", "Shayan", "Shaye", "Shayne", "Shazil", "Shea", "Sheafan", "Sheigh", "Shenuk", "Sher", "Shergo", "Sheriff", "Sherwyn", "Shiloh", "Shiraz", "Shreeram", "Shreyas", "Shyam", "Siddhant", "Siddharth", "Sidharth", "Sidney", "Siergiej", "Silas", "Simon", "Sinai", "Skye", "Sofian", "Sohaib", "Sohail", "Soham", "Sohan", "Sol", "Solomon", "Sonneey", "Sonni", "Sonny", "Sorley", "Soul", "Spencer", "Spondon", "Stanislaw", "Stanley", "Stefan", "Stefano", "Stefin", "Stephen", "Stephenjunior", "Steve", "Steven", "Steven-lee", "Stevie", "Stewart", "Stewarty", "Strachan", "Struan", "Stuart", "Su", "Subhaan", "Sudais", "Suheyb", "Suilven", "Sukhi", "Sukhpal", "Sukhvir", "Sulayman", "Sullivan", "Sultan", "Sung", "Sunny", "Suraj", "Surien", "Sweyn", "Syed", "Sylvain", "Symon", "Szymon", "Tadd", "Taddy", "Tadhg", "Taegan", "Taegen", "Tai", "Tait", "Taiwo", "Talha", "Taliesin", "Talon", "Talorcan", "Tamar", "Tamiem", "Tammam", "Tanay", "Tane", "Tanner", "Tanvir", "Tanzeel", "Taonga", "Tarik", "Tariq-Jay", "Tate", "Taylan", "Taylar", "Tayler", "Taylor", "Taylor-Jay", "Taylor-Lee", "Tayo", "Tayyab", "Tayye", "Tayyib", "Teagan", "Tee", "Teejay", "Tee-jay", "Tegan", "Teighen", "Teiyib", "Te-Jay", "Temba", "Teo", "Teodor", "Teos", "Terry", "Teydren", "Theo", "Theodore", "Thiago", "Thierry", "Thom", "Thomas", "Thomas-Jay", "Thomson", "Thorben", "Thorfinn", "Thrinei", "Thumbiko", "Tiago", "Tian", "Tiarnan", "Tibet", "Tieran", "Tiernan", "Timothy", "Timucin", "Tiree", "Tisloh", "Titi", "Titus", "Tiylar", "TJ", "Tjay", "T-Jay", "Tobey", "Tobi", "Tobias", "Tobie", "Toby", "Todd", "Tokinaga", "Toluwalase", "Tom", "Tomas", "Tomasz", "Tommi-Lee", "Tommy", "Tomson", "Tony", "Torin", "Torquil", "Torran", "Torrin", "Torsten", "Trafford", "Trai", "Travis", "Tre", "Trent", "Trey", "Tristain", "Tristan", "Troy", "Tubagus", "Turki", "Turner", "Ty", "Ty-Alexander", "Tye", "Tyelor", "Tylar", "Tyler", "Tyler-James", "Tyler-Jay", "Tyllor", "Tylor", "Tymom", "Tymon", "Tymoteusz", "Tyra", "Tyree", "Tyrnan", "Tyrone", "Tyson", "Ubaid", "Ubayd", "Uchenna", "Uilleam", "Umair", "Umar", "Umer", "Umut", "Urban", "Uri", "Usman", "Uzair", "Uzayr", "Valen", "Valentin", "Valentino", "Valery", "Valo", "Vasyl", "Vedantsinh", "Veeran", "Victor", "Victory", "Vinay", "Vince", "Vincent", "Vincenzo", "Vinh", "Vinnie", "Vithujan", "Vladimir", "Vladislav", "Vrishin", "Vuyolwethu", "Wabuya", "Wai", "Walid", "Wallace", "Walter", "Waqaas", "Warkhas", "Warren", "Warrick", "Wasif", "Wayde", "Wayne", "Wei", "Wen", "Wesley", "Wesley-Scott", "Wiktor", "Wilkie", "Will", "William", "William-John", "Willum", "Wilson", "Windsor", "Wojciech", "Woyenbrakemi", "Wyatt", "Wylie", "Wynn", "Xabier", "Xander", "Xavier", "Xiao", "Xida", "Xin", "Xue", "Yadgor", "Yago", "Yahya", "Yakup", "Yang", "Yanick", "Yann", "Yannick", "Yaseen", "Yasin", "Yasir", "Yassin", "Yoji", "Yong", "Yoolgeun", "Yorgos", "Youcef", "Yousif", "Youssef", "Yu", "Yuanyu", "Yuri", "Yusef", "Yusuf", "Yves", "Zaaine", "Zaak", "Zac", "Zach", "Zachariah", "Zacharias", "Zacharie", "Zacharius", "Zachariya", "Zachary", "Zachary-Marc", "Zachery", "Zack", "Zackary", "Zaid", "Zain", "Zaine", "Zaineddine", "Zainedin", "Zak", "Zakaria", "Zakariya", "Zakary", "Zaki", "Zakir", "Zakk", "Zamaar", "Zander", "Zane", "Zarran", "Zayd", "Zayn", "Zayne", "Ze", "Zechariah", "Zeek", "Zeeshan", "Zeid", "Zein", "Zen", "Zendel", "Zenith", "Zennon", "Zeph", "Zerah", "Zhen", "Zhi", "Zhong", "Zhuo", "Zi", "Zidane", "Zijie", "Zinedine", "Zion", "Zishan", "Ziya", "Ziyaan", "Zohaib", "Zohair", "Zoubaeir", "Zubair", "Zubayr", "Zuriel"};
			int pick = (int) (Math.random() * 2738);
			list[i] = new AIPlayer(names[pick], 5);
		}
		System.out.println("---Player 1---");
		System.out.print("Enter your name: ");
		String name = scan.nextLine();
		HumanPlayer humanPlayer = new HumanPlayer(name, 5);

		System.out.print("\n-Setting Up The Players-");
		scan.nextLine();
		list[0] = humanPlayer;
		for(int i = 0; i < playerCount; i++)
		{
			System.out.println(list[i]);
		}
		System.out.print("\n-Setting the hands-");
		deck.shuffle();
		scan.nextLine();

		for(int i = 0; i < playerCount; i++) //bots set up
		{
			list[i].setHand(deck);
			Deck.sortCards(list[i].getCards());
		}
		System.out.print("\n-Starting the game-");
		scan.nextLine();
	}
	public static Player winner(Player[] listGiven)
	{
		tiedWithHowMany = 0;
		String[] winning = new String[] {"High Card", "One Pair", "Two Pair", "Three of a Kind", "Straight", "Flush", "Full House", "Four of a Kind", "Straight Flush", "Royal Flush"};
		Player currentWinner = listGiven[0];
		for(int i = 1; i < listGiven.length; i++)
		{
			if(!list[i].isFold() && GameRules.scoreCards(currentWinner.getCards()) < GameRules.scoreCards(listGiven[i].getCards()))
			{
				win = winning[GameRules.scoreCards(currentWinner.getCards()) - 1]; //how they won is shown
				currentWinner = listGiven[i];
			}
			if(!list[i].isFold() && GameRules.scoreCards(currentWinner.getCards()) == GameRules.scoreCards(listGiven[i].getCards())) //if they have the same score
			{
				if(GameRules.breakTie(currentWinner.getCards(), listGiven[i].getCards()) == 1 || GameRules.breakTie(currentWinner.getCards(), listGiven[i].getCards()) == -1)
				{					
					if(GameRules.breakTie(currentWinner.getCards(), listGiven[i].getCards()) == -1)
					{
						tiedWith[tiedWithHowMany] = currentWinner; //keep adding
						tiedWithHowMany++;
						tiedWith[tiedWithHowMany] = list[i]; //keep adding
						tie = true;
					}
					currentWinner = listGiven[i]; //if it's a true tie the boolean tie will help
				}
			}
		}
		win = winning[GameRules.scoreCards(currentWinner.getCards()) - 1]; //how they won is shown
		return currentWinner; //the winner is returned
	}
	public static void timer()
	{
		try
		{
			Thread.sleep(1000);
		}
		catch(Exception e)
		{
			System.out.println("Bots have taken over... Uh oh!\nSystem crash.exe");
		}
	}
	public static void bet(Player player) //who starts the bet
	{
		Player raiser = player;
		if(!((HumanPlayer)player).isFold()) //make sure they haven't folded
		{
			if(!((HumanPlayer)player).isBot()) //not a bot
			{
				System.out.println("How much would you like to put in? (Type 0 to not raise)\n[Total: " + ((HumanPlayer)player).getMoney() + "]");
				System.out.print("   >> ");
				double amount = scan.nextDouble();
				while(amount > ((HumanPlayer)player).getMoney() || amount < 0)
				{
					System.out.println("How much would you like to put in? (Type 0 to not raise)\n[Total: " + ((HumanPlayer)player).getMoney() + "]");
					System.out.print("   >> ");
					amount = scan.nextDouble();
				}
				if(amount != 0)
				{
					((HumanPlayer)player).deduct(amount);
					((HumanPlayer)player).setRaise(amount); //they so far paid this much
					kitty.update(amount);
					performedBets(raiser, amount);
				}
			}
			else //if the bot is doing the bet
			{
				if(currentState == gameState.play) //only do this when you're betting
				{
					System.out.println("5");
				}
				double amount = ((AIPlayer)player).makeBet();
				timer();
				System.out.println(player.getName() + " raises the bet up by $" + amount);
				kitty.update(amount);
				((AIPlayer)player).setRaise(amount); //they've already paid this much
				performedBets(raiser, amount);
			}
		}
		else
		{
			System.out.println("Something wrong, this person already folded");
		}
	}
	public static void performedBets(Player raiser, double amount)
	{
		for(int i = 1; i < playerCount; i++) //go through everyone
		{
			if((HumanPlayer)list[i] == raiser) //reached the full curicle
			{
				break;
			}
			if(!((HumanPlayer)list[i]).isFold()) //only if they haven't folded
			{
				if(((HumanPlayer)list[i]).isBot()) //they are a bot
				{
					if(!((AIPlayer)list[i]).canCoverBet(amount - ((AIPlayer)list[i]).getRaise())) //can't cover the amount remaining
					{
						System.out.println(list[i].getName() + ", unfortunatly you are forced to fold\n[total money: " + ((AIPlayer)list[i]).getMoney() + "]");
						list[i].setFold(true);
						if(i == 0) //this is the dealer
						{
							dealer++;
						}
						break;
					}
					System.out.println(list[i].getName() + ", do you want to match the bet of $" + (amount - ((AIPlayer)list[i]).getRaise()) + "? (you have: $" + ((AIPlayer)list[i]).getMoney() + ")\n[1 - no and 2 - yes and 3 - raise]");
					System.out.print("   >> ");
					timer();
					int folded = ((AIPlayer)list[i]).matchBet(amount - ((AIPlayer)list[i]).getRaise());
					System.out.println(folded);
					if(folded == 1)//they did fold
					{
						list[i].setFold(true);
						if(list[i] == list[0]) //this is the dealer
						{
							dealer++;
						}
						System.out.println(list[i].getName() + " has folded");
					}
					else if(folded == 2)
					{
						kitty.update(amount - ((AIPlayer)list[i]).getRaise()); //keep on getting money
						((AIPlayer)list[i]).deduct(amount - ((AIPlayer)list[i]).getRaise());
						((AIPlayer)list[i]).setRaise(amount);
						System.out.println(list[i].getName() + " is still in the game (total: " + ((AIPlayer)list[i]).getMoney() + ")");
					}
					else
					{
						double raisedAmount = ((AIPlayer)list[i]).makeBet();
						System.out.println(list[i].getName() + " raises the bet up by $" + raisedAmount);
						amount += raisedAmount;
						kitty.update(amount - ((AIPlayer)list[i]).getRaise()); //already subtracts and adds the extra
						((AIPlayer)list[i]).deduct(amount - ((AIPlayer)list[i]).getRaise());
						((AIPlayer)list[i]).setRaise(amount); //they've already paid this much
						raiser = list[i];
					}
				}
				else //human!
				{
					if(((HumanPlayer)list[i]).canCoverBet(amount - ((HumanPlayer)list[i]).getRaise())) //only if they can cover
					{					
						System.out.println(list[i].getName() + ", do you want to match this bet of $" + (amount - ((HumanPlayer)list[i]).getRaise()) + "? (you have: $" + ((HumanPlayer)list[i]).getMoney() + ")\n[1 - no and 2 - yes and 3 - raise]");
						System.out.print("   >> ");
						int folded = scan.nextInt();
						while(folded != 1 && folded != 2 && folded != 3)
						{
							System.out.println("Do you want to match this? \n[1 - no and 2 - yes and 3 - raise]");
							System.out.print("   >> ");
							folded = scan.nextInt();
						}
						if(folded == 1)//they did fold
						{
							((HumanPlayer)list[i]).setFold(true);
							if(list[i] == list[0]) //this is the dealer
							{
								dealer++;
							}
							System.out.println(((HumanPlayer)list[i]).getName() + " has folded");
						}
						else if(folded == 2)
						{
							kitty.update(amount); //keep on getting money
							((HumanPlayer)list[i]).deduct(amount);
							((HumanPlayer)list[i]).setRaise(amount);
							System.out.println(((HumanPlayer)list[i]).getName() + " is still in the game (total: " + ((HumanPlayer)list[i]).getMoney() + ")");
						}
						else
						{
							System.out.println("How much would you like to raise?\n[Type 0 for no raise]\n[Total: $" + ((HumanPlayer)list[i]).getMoney() + "]");
							System.out.print("   >> ");
							double raisedAmount = scan.nextDouble();
							while(raisedAmount < 0 || raisedAmount + amount > ((HumanPlayer)list[i]).getMoney()) //they don't want to raise or raise is too high + what htye owe
							{
								System.out.println("How much would you like to raise?\n[Type 0 for no raise]\n[Total: $" + ((HumanPlayer)list[i]).getMoney() + "]");
								System.out.print("   >> ");
								raisedAmount = scan.nextDouble();
							}
							if(raisedAmount == 0) //just keep rolling
							{
								kitty.update(amount); //keep on getting money
								((HumanPlayer)list[i]).deduct(amount);
								System.out.println(((HumanPlayer)list[i]).getName() + " is still in the game (total: " + ((HumanPlayer)list[i]).getMoney() + ")");
							}
							else //raised
							{
								amount += raisedAmount;
								kitty.update(amount - ((HumanPlayer)list[i]).getRaise()); //already subtracts and adds the extra
								((HumanPlayer)list[i]).deduct(amount - ((HumanPlayer)list[i]).getRaise());
								((HumanPlayer)list[i]).setRaise(amount); //they've already paid this much
								raiser = list[i];
							}
						}
					}
					else //cannot cover
					{
						System.out.println(list[i].getName() + ", unfortunatly you are forced to fold\n[total money: " + ((HumanPlayer)list[i]).getMoney() + "]");
						list[i].setFold(true);
						if(i == 0) //this is the dealer
						{
							dealer++;
						}
					}
				}
			}
			if(i == playerCount - 1)
			{
				i = -1; //it will add one and will go back to first person
			}
		}
		for(int k = 0; k < playerCount; k++)
		{
			((HumanPlayer)list[k]).setRaise(0); //reset so they haven't put anything in the pot
		}
	}
}