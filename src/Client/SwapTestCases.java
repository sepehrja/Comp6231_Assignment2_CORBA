package Client;

import ServerObjectInterfaceApp.ServerObjectInterface;
import ServerObjectInterfaceApp.ServerObjectInterfaceHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class SwapTestCases {
    public static final String CONFERENCES = "Conferences";
    public static final String SEMINARS = "Seminars";
    public static final String TRADE_SHOWS = "Trade Shows";

    public static void main(String[] args) throws Exception {

        try {
            ORB orb = ORB.init(args, null);
            // -ORBInitialPort 1050 -ORBInitialHost localhost
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            startTest(ncRef);
        } catch (Exception e) {
            System.out.println("Client ORB init exception: " + e);
            e.printStackTrace();
        }
    }

    private synchronized static void startTest(NamingContextExt ncRef) throws Exception {
        ServerObjectInterface MTLobj = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str("MTL"));
        ServerObjectInterface QUEobj = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str("QUE"));
        ServerObjectInterface SHEobj = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str("SHE"));

        System.out.println("TestCases Started for Swap");
        System.out.println("*********************************************************");
        /**
         * pre-added Test Cases file
         * --------------------------
         * QUEC1234 has booked:
         * SHEE150620 - CONFERENCES
         * SHEE160620 - SEMINARS
         * MTLA160620 - CONFERENCES
         * QUEA150620 - CONFERENCES
         *
         * SHEC1234 has booked:
         * MTLA170620 - TRADE_SHOWS
         *
         * events available:
         * QUEA150620 - CONFERENCES (0)
         * MTLA160620 - CONFERENCES (1)
         * SHEE150620 - CONFERENCES (1)
         *
         * MTLA170620 - TRADE_SHOWS (0)
         * MTLA150620 - TRADE_SHOWS (1)
         *
         * QUEA160620 - SEMINARS (1)
         * SHEE160620 - SEMINARS (0)
         * */

        System.out.println("Case0 assuming the new event has no capacity:");
        System.out.println("<Fail>");
        System.out.println(QUEobj.swapEvent("QUEC1234", "MTLA170620", TRADE_SHOWS, "SHEE150620", CONFERENCES));
        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
        System.out.println("*********************************************************");
        System.out.println("In all the below cases we are assuming that the new event has the capacity");
        System.out.println("Also the weekly limit is assumed to be for the newEvent");
        System.out.println("Also in every Success situation we rolled back the result to before the swap to have consistent data to work with");
        System.out.println("*********************************************************");

//        System.out.println("Case1 OldEvent Does not exist-newEvent exists:");
//        System.out.println("<Fail>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case2 OldEvent exists-newEvent does not exist:");
//        System.out.println("<Fail>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case3 OldEvent and newEvent in user city:");
//        System.out.println("(OldEvent and newEvent in same week)");
//        System.out.println("<Success>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case4 OldEvent and newEvent in user city:");
//        System.out.println("(OldEvent and newEvent NOT in same week)");
//        System.out.println("<Success>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case5 OldEvent NOT in user city and newEvent in user city:");
//        System.out.println("(OldEvent and newEvent in same week)");
//        System.out.println("<Success>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case6 OldEvent NOT in user city and newEvent in user city:");
//        System.out.println("(OldEvent and newEvent NOT in same week)");
//        System.out.println("<Success>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case7 OldEvent in user city and newEvent in OTHER city:");
//        System.out.println("(in same week && limit == 3)");
//        System.out.println("<Fail>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case8 OldEvent in user city and newEvent in OTHER city:");
//        System.out.println("(in same week && limit < 3)");
//        System.out.println("<Success>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case9 OldEvent in user city and newEvent in OTHER city:");
//        System.out.println("(NOT in same week && limit == 3)");
//        System.out.println("<Fail>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case10 OldEvent in user city and newEvent in OTHER city:");
//        System.out.println("(NOT in same week && limit < 3)");
//        System.out.println("<Success>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case11 OldEvent and newEvent in OTHER city:");
//        System.out.println("(in same week && limit == 3)");
//        System.out.println("<Success>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case12 OldEvent and newEvent in OTHER city:");
//        System.out.println("(in same week && limit < 3)");
//        System.out.println("<Success>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case13 OldEvent and newEvent in OTHER city:");
//        System.out.println("(NOT in same week && limit == 3)");
//        System.out.println("<Fail>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");
//
//        System.out.println("Case14 OldEvent and newEvent in OTHER city:");
//        System.out.println("(NOT in same week && limit < 3)");
//        System.out.println("<Success>");
//        System.out.println(QUEobj.swapEvent("QUEC1234",));
//        System.out.println(QUEobj.getBookingSchedule("QUEC1234"));
//        System.out.println("*********************************************************");


    }
}
