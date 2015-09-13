
public class Entry {

	public static void main(String[] args) {

		AkkaController controller = new AkkaController();

		try {
			controller.execute();
		} catch (Exception e) {
			e.printStackTrace();

			System.exit(1);
		}

		System.exit(0);
	}

}
