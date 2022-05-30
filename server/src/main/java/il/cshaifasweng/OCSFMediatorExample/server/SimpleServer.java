package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class SimpleServer extends AbstractServer {
	private static Session session;
	static Scanner sc = new Scanner(System.in);
	static HashSet<String> connected = new HashSet<>();

	public SimpleServer(int port) {
		super(port);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) throws IOException,Exception {
		ArrayList<Object> arr = new ArrayList<>();
		try {
			session = App.getSession().openSession();
		 	session.beginTransaction();
			CatalogController catalogController = new CatalogController(session);
			ChainManagerController chainManagerController = new ChainManagerController(session);
			ComplaintController complaintController = new ComplaintController(session);
			EmployeeController employeeController = new EmployeeController(session);
			FlowerController flowerController = new FlowerController(session);
			OrderController orderController = new OrderController(session);
			RefundController refundController = new RefundController(session);
			StoreManagerController storeManagerController = new StoreManagerController(session);
			UserController userController = new UserController(session);

			arr = (ArrayList<Object>) msg;
			ArrayList<Object> answers = new ArrayList<>();

			// get catalog for everyone
			if((arr.get(0)).equals("#getcatalog")) {
				ArrayList<Flower> list = (ArrayList<Flower>) flowerController.getAllData(Flower.class);
				answers.add("#getcatalog");
				answers.add(list);
				sendToAllClients(answers);
			}
			// sign up a new user
			if((arr.get(0)).equals("#register")) {
				User user = new User((String)arr.get(1), (String)arr.get(2), (String)arr.get(3),
									 (String)arr.get(4), (String)arr.get(5), (String)arr.get(6),
									 (String)arr.get(7), (String)arr.get(8), (String)arr.get(9),
									 (String)arr.get(10), (double)arr.get(11));
				userController.addUser(user);
				session.getTransaction().commit();

				List<User> list = userController.getAllData(User.class);
				String eMail = "", password = "";
				String myMail = (String)arr.get(3);
				String myPassword = (String)arr.get(8);

				for(int i = list.size() - 1; i >= 0; i++) {
					eMail = list.get(i).getEmail();
					if(eMail.equals(myMail)) {
						password = list.get(i).getPassword();
						if(password.equals(myPassword)) {
							answers.add("#connectUserAfterRegistration");
							answers.add(list.get(i).getName());
							answers.add(list.get(i).getId());
							answers.add(list.get(i).getEmail());
							answers.add(list.get(i).getPhone());
							answers.add(list.get(i).getCredit());
							answers.add(list.get(i).getMonthAndYear());
							answers.add(list.get(i).getCvv());
							answers.add(list.get(i).getPassword());
							answers.add(list.get(i).getAccount());
							answers.add(list.get(i).getStoreOrNull());
							answers.add(list.get(i).getRefund());
							answers.add(list.get(i).getID());
							client.sendToClient(answers);
						}
					}
					break;
				}
			}
			// login for users, employees, store Managers, Chain manager
			if((arr.get(0)).equals("#loginUser")) {
				if((arr.get(1)).equals("User")) {
					List<User> list = userController.getAllData(User.class);
					loginUser(arr, list, answers);
				}
				else if((arr.get(1)).equals("Employee")) {
					List<Employee> list = employeeController.getAllData(Employee.class);
					loginEmployee(arr, list, answers);
				}
				else if((arr.get(1)).equals("Store Manager")) {
					List<StoreManager> list = storeManagerController.getAllData(StoreManager.class);
					loginStoreM(arr, list, answers);
				}
				else if((arr.get(1)).equals("Chain Manager")) {
					List<ChainManager> list = chainManagerController.getAllData(ChainManager.class);
					loginChainM(arr, list, answers);
				}
				client.sendToClient(answers);
			}
			// add order (pick up + delivery)
			if((arr.get(0)).equals("#addOrder")) {
				Order item = new Order((int)arr.get(1), (String)arr.get(2), (String)arr.get(3),
										(String)arr.get(4), (String)arr.get(5), (String)arr.get(6),
										(String)arr.get(7), (String)arr.get(8), (String)arr.get(9),
										(double)arr.get(10), (String)arr.get(11), (String)arr.get(12),
										(String)arr.get(13), (int)arr.get(14), (int)arr.get(15),
										(double)arr.get(16));
				orderController.addOrder(item);
				session.getTransaction().commit();
			}
			// update personal details
			if((arr.get(0)).equals("#updateDetails")) {
				User user = new User((String)arr.get(1), (String)arr.get(2), (String)arr.get(3),
									 (String)arr.get(4), (String)arr.get(5), (String)arr.get(6),
									 (String)arr.get(7), (String)arr.get(8), (String)arr.get(9),
									 (String)arr.get(10), (double)arr.get(11));
				int currentID = (int)arr.get(12);
				userController.updateData(currentID, user);
				session.getTransaction().commit();
			}
			// get my orders
			if((arr.get(0)).equals("#getmyorders")) {
				ArrayList<Order> list = (ArrayList<Order>)orderController.getAllData(Order.class);
				answers.add("#getmyorders");
				answers.add((int)arr.get(1));
				answers.add(list);
				client.sendToClient(answers);
			}
			// cancel an order
			if((arr.get(0)).equals("#cancelorder")) {
				List<Order> orderList = orderController.getAllData(Order.class);
				List<User> userList = userController.getAllData(User.class);

				int myUserId = (int)arr.get(1);
				int myOrderId = (int)arr.get(2);
				String cancellationTime = (String)arr.get(3);
				int status = (int)arr.get(4);
				double refundNow = (double)arr.get(5);
				double sum = 0, percentageRefund, userRefund;

				for(int i = 0; i < orderList.size(); i++) {
					if(myOrderId == orderList.get(i).getOrderID()) {
						if(myUserId == orderList.get(i).getUserID()) {
							if(orderList.get(i).getStatus() == 1) {
								// order update:
								int realOrderID = orderList.get(i).getId();
								orderList.get(i).setStatus(status);
								orderController.updateData(realOrderID, orderList.get(i));
								// user update:
								String supplyTime = orderList.get(i).getDateTime();
								sum = orderList.get(i).getFinalPrice() + orderList.get(i).getRefund();
								percentageRefund = calculateNewRefund(cancellationTime, supplyTime, sum);
								userRefund = refundNow + percentageRefund;
								userList.get(myUserId - 1).setRefund(userRefund);
								userController.updateData(myUserId, userList.get(myUserId - 1));
								// create refund in database
								Refund r = new Refund(myOrderId, percentageRefund, myUserId);
								refundController.addRefund(r);
								session.getTransaction().commit();

								answers.add("#cancelOrder");
								answers.add(userRefund);
								client.sendToClient(answers);
							}
						}
					}
				}
			}
			// file complaints
			if((arr.get(0)).equals("#newComplaint")) {
				List<Order> list = orderController.getAllData(Order.class);
				int myUserId = (int)arr.get(1);
				int myOrderId = (int)arr.get(2);

				for(int i = 0; i < list.size(); i++) {
					if(myOrderId == list.get(i).getOrderID()) {
						if(myUserId == list.get(i).getUserID()) {
							Complaint complaint = new Complaint((int)arr.get(1), (int)arr.get(2),
																(String)arr.get(3), (String)arr.get(4),
																(int) arr.get(5));
							complaintController.addComplaint(complaint);
							session.getTransaction().commit();
							answers.add("#complaintAdded");
							client.sendToClient(answers);
						}
					}
				}
			}
			// get refund history
			if((arr.get(0)).equals("#getmyrefunds")) {
				ArrayList<Refund> list = (ArrayList<Refund>)refundController.getAllData(Refund.class);
				answers.add("#getmyrefunds");
				answers.add((int)arr.get(1));
				answers.add(list);
				client.sendToClient(answers);
			}
			// add flower - employee
			if((arr.get(0)).equals("#addFlower")) {
				int serialN = App.getFlowerSerialNumber();
				App.setFlowerSerialNumber(serialN + 1);
				Catalog c = App.getCatalog();
				Flower f = new Flower((String)arr.get(1), (String)arr.get(2), (String)arr.get(3),
									  (String)arr.get(4), (String)arr.get(5), (double)arr.get(6),
									  serialN, (boolean)arr.get(7), (double)arr.get(8));
				f.setCatalog(c);
				flowerController.addFlower(f);
				session.getTransaction().commit();
			}
			// delete flower - employee
			if((arr.get(0)).equals("#deleteflower")) {
				List<Flower> list = flowerController.getAllData(Flower.class);
				int newID = (int)arr.get(1);
				int newID1 = newID + 1;
				list.get(newID).setStatus(2);
				flowerController.updateData(newID1, list.get(newID));
				session.getTransaction().commit();
			}
			// before update flower - employee
			if((arr.get(0)).equals("#beforeUpdate")) {
				List<Flower> list = flowerController.getAllData(Flower.class);
				int newID = (int)arr.get(1);
				int newID1 = newID + 1;

				answers.add("#beforeUpdate");
				answers.add(list.get(newID).getName());
				answers.add(list.get(newID).getDescription());
				answers.add(list.get(newID).getType());
				answers.add(list.get(newID).getImageurl());
				answers.add(list.get(newID).getColor());
				answers.add(list.get(newID).getPrice());
				answers.add(list.get(newID).getSerialNumber());
				answers.add(list.get(newID).getSale());
				answers.add(list.get(newID).getDiscount());
				answers.add(list.get(newID).getStatus());
				answers.add(newID1);
				client.sendToClient(answers);
			}
			// update flower - employee
			if((arr.get(0)).equals("#updateFlower")) {
				Flower f = new Flower((String)arr.get(1), (String)arr.get(2), (String)arr.get(3),
									  (String)arr.get(4), (String)arr.get(5), (double)arr.get(6),
									  (int)arr.get(7), (boolean)arr.get(8), (double)arr.get(9),
									  (int)arr.get(10));
				int id = (int)arr.get(11);
				flowerController.updateData(id, f);
				session.getTransaction().commit();
			}
			// order list - employee
			if((arr.get(0)).equals("#orderListE")) {
				ArrayList<Order> list = (ArrayList<Order>)orderController.getAllData(Order.class);
				answers.add("#orderListE");
				answers.add((String)arr.get(1));
				answers.add((int)arr.get(2));
				answers.add(list);
				client.sendToClient(answers);
			}
			// supply order - employee
			if((arr.get(0)).equals("#supplyOrder")) {
				List<Order> list = orderController.getAllData(Order.class);
				int newID = (int)arr.get(1);
				int newID1 = newID + 1;
				list.get(newID).setStatus(2);
				orderController.updateData(newID1, list.get(newID));
				session.getTransaction().commit();
			}
			// disconnecting
			if((arr.get(0)).equals("#disconnecting")) {
				String Email = (String)arr.get(1);
				connected.remove(Email);
			}
		}
		catch (Exception exception) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.err.println("An error occured, changes have been rolled back.");
			exception.printStackTrace();
		}
		finally {
			session.close();
		}
	}

	public void loginUser(ArrayList<Object> arr, List<User> list, ArrayList<Object> answers) {
		String eMail = "", password = "";
		String myMail = (String)arr.get(2);
		String myPassword = (String)arr.get(3);

		for(int i = 0; i < list.size(); i++) {
			eMail = list.get(i).getEmail();
			if(eMail.equals(myMail)) {
				password = list.get(i).getPassword();
				if(password.equals(myPassword)) {
					if(connected.contains(eMail)) {
						answers.add("#connectEntity");
						answers.add(false);
						return;
					}
					answers.add("#connectEntity");
					answers.add(true);
					answers.add(arr.get(1));
					answers.add(list.get(i).getName());
					answers.add(list.get(i).getId());
					answers.add(list.get(i).getEmail());
					answers.add(list.get(i).getPhone());
					answers.add(list.get(i).getCredit());
					answers.add(list.get(i).getMonthAndYear());
					answers.add(list.get(i).getCvv());
					answers.add(list.get(i).getPassword());
					answers.add(list.get(i).getAccount());
					answers.add(list.get(i).getStoreOrNull());
					answers.add(list.get(i).getRefund());
					answers.add(list.get(i).getID());
					connected.add(eMail);
					return;
				}
			}
		}
	}

	public void loginEmployee(ArrayList<Object> arr, List<Employee> list, ArrayList<Object> answers) {
		String eMail = "", password = "";
		String myMail = (String)arr.get(2);
		String myPassword = (String)arr.get(3);

		for(int i = 0; i < list.size(); i++) {
			eMail = list.get(i).getEmail();
			if(eMail.equals(myMail)) {
				password = list.get(i).getPassword();
				if(password.equals(myPassword)) {
					if(connected.contains(eMail)) {
						answers.add("#connectEntity");
						answers.add(false);
						return;
					}
					answers.add("#connectEntity");
					answers.add(true);
					answers.add(arr.get(1));
					answers.add(list.get(i).getName());
					answers.add(list.get(i).getEmail());
					answers.add(list.get(i).getPassword());
					answers.add(list.get(i).getId());
					connected.add(eMail);
					return;
				}
			}
		}
	}

	public void loginStoreM(ArrayList<Object> arr, List<StoreManager> list, ArrayList<Object> answers) {
		String eMail = "", password = "";
		String myMail = (String)arr.get(2);
		String myPassword = (String)arr.get(3);

		for(int i = 0; i < list.size(); i++) {
			eMail = list.get(i).getEmail();
			if(eMail.equals(myMail)) {
				password = list.get(i).getPassword();
				if(password.equals(myPassword)) {
					if(connected.contains(eMail)) {
						answers.add("#connectEntity");
						answers.add(false);
						return;
					}
					answers.add("#connectEntity");
					answers.add(true);
					answers.add(arr.get(1));
					answers.add(list.get(i).getName());
					answers.add(list.get(i).getEmail());
					answers.add(list.get(i).getPassword());
					answers.add(list.get(i).getStoreName());
					answers.add(list.get(i).getId());
					connected.add(eMail);
					return;
				}
			}
		}
	}

	public void loginChainM(ArrayList<Object> arr, List<ChainManager> list, ArrayList<Object> answers) {
		String eMail = "", password = "";
		String myMail = (String)arr.get(2);
		String myPassword = (String)arr.get(3);

		for(int i = 0; i < list.size(); i++) {
			eMail = list.get(i).getEmail();
			if(eMail.equals(myMail)) {
				password = list.get(i).getPassword();
				if(password.equals(myPassword)) {
					if(connected.contains(eMail)) {
						answers.add("#connectEntity");
						answers.add(false);
						return;
					}
					answers.add("#connectEntity");
					answers.add(true);
					answers.add(arr.get(1));
					answers.add(list.get(i).getName());
					answers.add(list.get(i).getEmail());
					answers.add(list.get(i).getPassword());
					answers.add(list.get(i).getId());
					connected.add(eMail);
					return;
				}
			}
		}
	}

	public double calculateNewRefund(String cancellationTime, String supplyTime, double refund) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		long date1 = formatter.parse(cancellationTime).getTime();
		long date2 = formatter.parse(supplyTime).getTime();

		// Get msec from each, and subtract.
		long diff = date2 - date1;
		// Difference In minutes:
		double percentage = (diff / (1000 * 60));

		// 1 - if 100% refund, 0.5 for 50%, 0- no refund
		if (percentage > 180) {
			return 1 * refund;
		}
		else if (percentage < 60) {
			return 0 * refund;
		}
		else {
			return 0.5 * refund;
		}
	}

	@Override
	protected synchronized void clientDisconnected(ConnectionToClient client) {
		// TODO Auto-generated method stub
		System.out.println("Client Disconnected.");
		super.clientDisconnected(client);
	}

	@Override
	protected void clientConnected(ConnectionToClient client) {
		super.clientConnected(client);
		System.out.println("Client connected: " + client.getInetAddress());
	}

	private static <T> List<T> getAllData(Class<T> c) throws Exception {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = builder.createQuery(c);
		Root<T> rootEntry = criteriaQuery.from(c);
		CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);
		TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
		return allQuery.getResultList();
	}

	public static void main(String[] args) throws IOException,Exception {
		if (args.length != 1) {
			System.out.println("Required argument: <port>");
		}
		else {
			System.out.println("please enter the port number: ");
			SimpleServer server = new SimpleServer(sc.nextInt());
			server.listen();
		}
	}
}
