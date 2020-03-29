package Server;

import ServerObjectInterfaceApp.ServerObjectInterface;
import ServerObjectInterfaceApp.ServerObjectInterfaceHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class TestData {
	public static final String EVENT_TIME_MORNING = "Morning";
	public static final String EVENT_TIME_AFTERNOON = "Afternoon";
	public static final String EVENT_TIME_EVENING = "Evening";
	public static final String CONFERENCES = "Conferences";
	public static final String SEMINARS = "Seminars";
	public static final String TRADE_SHOWS = "Trade Shows";

	public static void main(String[] args) throws Exception {

		try {
			ORB orb = ORB.init(args, null);
			// -ORBInitialPort 1050 -ORBInitialHost localhost
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			addData(ncRef);
		} catch (Exception e) {
			System.out.println("Client ORB init exception: " + e);
			e.printStackTrace();
		}
	}

	private static void addData(NamingContextExt ncRef) throws Exception {
		ServerObjectInterface MTLobj = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str("MTL"));
		ServerObjectInterface QUEobj = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str("QUE"));
		ServerObjectInterface SHEobj = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str("SHE"));

		System.out.println("Adding Data");
		System.out.println("*********************************************************");

		SHEobj.addEvent("SHEE110620", CONFERENCES, 1);
		SHEobj.addEvent("SHEE080620", CONFERENCES, 1);

		MTLobj.addEvent("MTLA090620", CONFERENCES, 2);
		MTLobj.addEvent("MTLA080620", TRADE_SHOWS, 2);
		MTLobj.addEvent("MTLE230620", SEMINARS, 1);
		MTLobj.addEvent("MTLA150620", TRADE_SHOWS, 12);

		QUEobj.bookEvent("QUEC1234", "SHEE080620", CONFERENCES);
		QUEobj.bookEvent("QUEC1234", "MTLA090620", CONFERENCES);
		QUEobj.bookEvent("QUEC1234", "MTLA080620", TRADE_SHOWS);

		QUEobj.bookEvent("QUEC4114", "MTLA080620", TRADE_SHOWS);

		System.out.println();
		System.out.println("Testing Started");
		System.out.println("*********************************************************");

		System.out.println("Test1");
		System.out.println("Quebec Manager listEventAvailability:");
		System.out.println(QUEobj.listEventAvailability(CONFERENCES));
		System.out.println(QUEobj.listEventAvailability(SEMINARS));
		System.out.println(QUEobj.listEventAvailability(TRADE_SHOWS));
		System.out.println("*********************************************************");

		System.out.println("Test2");
		System.out.println("QUEC1234 bookEvent:");
		System.out.println(QUEobj.bookEvent("QUEC1234", "SHEE110620", CONFERENCES));
		System.out.println("QUEC1234 bookEvent:");
		System.out.println(QUEobj.bookEvent("QUEC1234", "MTLEE230620", SEMINARS));
		System.out.println("*********************************************************");

		System.out.println("Test3");
		System.out.println("QUEC1234 cancelEvent:");
		System.out.println(QUEobj.cancelEvent("QUEC1234", "MTLA090620", CONFERENCES));
		System.out.println("*********************************************************");

		System.out.println("Test4");
		System.out.println("SHEC2345 bookEvent:");
		System.out.println(SHEobj.bookEvent("SHEC2345", "SHEE080620", CONFERENCES));
		System.out.println("*********************************************************");

		System.out.println("Test5");
		System.out.println("Montreal Manager removeEvent:");
		System.out.println(MTLobj.removeEvent("MTLA080620", TRADE_SHOWS));
		System.out.println("*********************************************************");

		System.out.println("Test6");
		System.out.println("SHEC2345 Booking Schedule:");
		System.out.println(SHEobj.getBookingSchedule("SHEC2345"));
		System.out.println("QUEC1234 Booking Schedule:");
		System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
		System.out.println("*********************************************************");

		System.out.println("Test7");
		System.out.println("Sherebrook Manager removeEvent:");
		System.out.println(SHEobj.listEventAvailability(CONFERENCES));
		System.out.println(SHEobj.listEventAvailability(SEMINARS));
		System.out.println(SHEobj.listEventAvailability(TRADE_SHOWS));
		System.out.println("*********************************************************");
	}
}
