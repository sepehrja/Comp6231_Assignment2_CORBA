package Server;

import ServerObjectInterfaceApp.ServerObjectInterface;
import ServerObjectInterfaceApp.ServerObjectInterfaceHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class TestData {
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

		//MTL
//		System.out.println(MTLobj.addEvent("Conferences", "MTLM230320", 1));
//		System.out.println(MTLobj.addEvent( "Seminars", "MTLM250320", 2));
//		MTLobj.printDatabase();

		System.out.println(MTLobj.addEvent("Trade_Shows", "MTLM230320", 3));
		System.out.println(MTLobj.addEvent("Trade_Shows", "MTLE230320", 3));

		//SHE
//		System.out.println(SHEobj.addEvent("Conferences", "SHEM110320", 1));
//		System.out.println(SHEobj.addEvent("Seminars", "SHEM120320", 2));
//		System.out.println(SHEobj.addEvent("Seminars", "SHEM130320", 3));
//		System.out.println(SHEobj.addEvent("Seminars", "SHEM100320", 4));
//		SHEobj.addEvent( "Seminars", "MTLM250320", 2);
//
//
//		System.out.println(MTLobj.toString());
//		System.out.println(MTLobj.bookEvent("MTLC1234", "SHEM110320", "Conferences"));
//		System.out.println(MTLobj.bookEvent("MTLC1234", "SHEM120320", "Seminars"));
//		System.out.println(MTLobj.bookEvent("MTLC1234", "SHEM130320", "Seminars"));
//		System.out.println(MTLobj.bookEvent("MTLC1234", "SHEM100320", "Seminars"));
//		//System.out.println(MTLobj.bookEvent("MTLC1234", "SHEM270320", "Seminars"));

		System.out.println(MTLobj.bookEvent("MTLC1234", "MTLM230320", "Trade_Shows"));
		System.out.println(MTLobj.bookEvent("MTLC1235", "MTLM230320", "Trade_Shows"));
		System.out.println(MTLobj.bookEvent("SHEC1236", "MTLM230320", "Trade_Shows"));


		//System.out.println(MTLobj.getBookingSchedul("MTLC1234"));
	}
}
