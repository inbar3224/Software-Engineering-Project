package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

public class SimpleClient extends AbstractClient {
	private static final Logger LOGGER = Logger.getLogger(SimpleClient.class.getName());
	static Scanner sc = new Scanner(System.in);
	private static SimpleClient client = null;

	public SimpleClient(String host, int port) {
		super(host, port);
	}

	public static SimpleClient getClient(String a, String b) {
		if (client == null) {
<<<<<<< Updated upstream
			System.out.println("please enter the IP address and then the port number: ");
			client = new SimpleClient(sc.next(), sc.nextInt());
=======
//			System.out.println("please enter the IP address and then the port number: ");
//			client = new SimpleClient(sc.next(), sc.nextInt());
			client = new SimpleClient(a, Integer.parseInt(b));
>>>>>>> Stashed changes
		}
		return client;
	}

	@Override
	protected void connectionEstablished() {
		// TODO Auto-generated method stub
		super.connectionEstablished();
		LOGGER.info("Connected to server.");
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		ArrayList<Flower> arr = new ArrayList<>();
		ArrayList<Object> msgArray = new ArrayList<>();
		msgArray = (ArrayList<Object>) msg;

		// get catalog for everyone
		if(msgArray.get(0).equals("#getcatalog")) {
			CatalogBoundaryController.setFlowers((ArrayList<Flower>)(msgArray.get(1)));
			CatalogEmployeeController.setFlowers((ArrayList<Flower>)(msgArray.get(1)));
			OrdersReportsController.setFlowers((ArrayList<Flower>)(msgArray.get(1)));
			OrdersReportsChainController.setFlowers((ArrayList<Flower>)(msgArray.get(1)));

		}
		if(msgArray.get(0).equals("#getorders")) {
			IncomeReportsController.setOrders((ArrayList<Order>)(msgArray.get(1)));
			IncomeReportsChainController.setOrders((ArrayList<Order>)(msgArray.get(1)));
			OrdersReportsController.setOrders((ArrayList<Order>)(msgArray.get(1)));
			OrdersReportsChainController.setOrders((ArrayList<Order>)(msgArray.get(1)));
		}
		if(msgArray.get(0).equals("#getcomplaints")) {
			ComplaintsReportsController.setComplaints((ArrayList<Complaint>)(msgArray.get(1)));
			ComplaintsReportsChainController.setComplaints((ArrayList<Complaint>)(msgArray.get(1)));

		}
		// sign up a new user
		if(msgArray.get(0).equals("#connectUserAfterRegistration")) {
			EntityHolder.setTable(0);
			User user = new User((String)msgArray.get(1), (String)msgArray.get(2),
								 (String)msgArray.get(3), (String)msgArray.get(4),
								 (String)msgArray.get(5), (String)msgArray.get(6),
								 (String)msgArray.get(7), (String)msgArray.get(8),
								 (String)msgArray.get(9), (String)msgArray.get(10),
								 (double)msgArray.get(11));
			int id = (int)msgArray.get(12);
			EntityHolder.setUser(user);
			EntityHolder.setID(id);
			RegistrationBoundaryController r = new RegistrationBoundaryController();
			r.nextStep();
		}
		// login for users, employees, store Managers, Chain manager
		if(msgArray.get(0).equals("#connectEntity")) {
			if((boolean)msgArray.get(1) == true) {
				if(msgArray.get(2).equals("User")) {
					EntityHolder.setTable(0);
					User user = new User((String)msgArray.get(3), (String)msgArray.get(4),
										 (String)msgArray.get(5), (String)msgArray.get(6),
										 (String)msgArray.get(7), (String)msgArray.get(8),
										 (String)msgArray.get(9), (String)msgArray.get(10),
										 (String)msgArray.get(11), (String)msgArray.get(12),
										 (double)msgArray.get(13));
					int id = (int)msgArray.get(14);
					EntityHolder.setUser(user);
					EntityHolder.setID(id);
					LoginBoundaryController loginController = new LoginBoundaryController();
					loginController.nextStep(2);
				}
				else if(msgArray.get(2).equals("Employee")) {
					EntityHolder.setTable(1);
					Employee employee = new Employee((String)msgArray.get(3),
													 (String)msgArray.get(4),
													 (String)msgArray.get(5));
					int id = (int)msgArray.get(6);
					EntityHolder.setEmployee(employee);
					EntityHolder.setID(id);
					LoginBoundaryController loginController = new LoginBoundaryController();
					loginController.nextStep(3);
				}
				else if(msgArray.get(2).equals("Store Manager")) {
					EntityHolder.setTable(2);
					StoreManager storeManager = new StoreManager((String)msgArray.get(3),
																 (String)msgArray.get(4),
																 (String)msgArray.get(5),
																 (String)msgArray.get(6));
					int id = (int)msgArray.get(7);
					EntityHolder.setStoreM(storeManager);
					EntityHolder.setID(id);
					LoginBoundaryController loginController = new LoginBoundaryController();
					loginController.nextStep(4);
				}
				else if(msgArray.get(2).equals("Chain Manager")) {
					EntityHolder.setTable(3);
					ChainManager chainManager = new ChainManager((String)msgArray.get(3),
																 (String)msgArray.get(4),
																 (String)msgArray.get(5));
					int id = (int)msgArray.get(6);
					EntityHolder.setChainM(chainManager);
					EntityHolder.setID(id);
					LoginBoundaryController loginController = new LoginBoundaryController();
					loginController.nextStep(5);
				}
			}
			else {
				if(msgArray.get(2).equals("connected"))
				{
					LoginBoundaryController loginController = new LoginBoundaryController();
					loginController.nextStep(6);
				}

			}
		}
		// add order (pick up + delivery)
		if(msgArray.get(0).equals("#addOrder")) {
			OrderPickUpBoundaryController o = new OrderPickUpBoundaryController();
			o.nextStep();
		}
		// update personal details
		if(msgArray.get(0).equals("detailsAreUpdated")) {
			PersonalDetailsBoundaryController p = new PersonalDetailsBoundaryController();
			p.nextStep();
		}
		// get my orders
		if(msgArray.get(0).equals("#getmyorders")) {
			int myUserId = (int)msgArray.get(1);
			ArrayList<Order> list = (ArrayList<Order>)msgArray.get(2);
			int generalUserId = 0;
			ObservableList<OrderHolder> newList = FXCollections.observableArrayList();

			for(int i = 0; i < list.size(); i++) {
				generalUserId = list.get(i).getUserID();
				if(generalUserId == myUserId) {
					OrderHolder o = new OrderHolder(list.get(i).getOrderID(), list.get(i).getDateTime(),
													list.get(i).getFinalPrice(), list.get(i).getFlowers(),
													list.get(i).getStatus());
					newList.add(o);
				}
			}

			MyOrdersBoundaryController.setMyOrders(newList);
			MyProfileBoundaryController m = new MyProfileBoundaryController();
			m.nextStep();
		}
		// cancel an order

		// file complaints
		if(msgArray.get(0).equals("#complaintAdded")) {
			FileComplaintBoundaryController f = new FileComplaintBoundaryController();
			boolean a = (boolean)msgArray.get(1);
			if(a == true) {
				f.nextStep();
			}
		}
		// add flower - employee

		// delete flower - employee

		// update flower - employee
	}

	@Override
	protected void connectionClosed() {
		// TODO Auto-generated method stub
		super.connectionClosed();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("Required arguments: <host> <port>");
		}
		else {
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			SimpleClient Client = new SimpleClient(host, port);
			Client.openConnection();
		}
	}
}