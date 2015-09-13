import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;


public class FirstActor extends UntypedActor {

	/**
	 * インスタンス化される際に1度のみ実行される(costructor)
	 */
	@Override
	public void preStart() {
		// 何か初期化処理
	}

	/**
	 * インスタンスが破棄される際に1度のみ実行される(destructor)
	 */
	@Override
	public void postStop() {
		// 何か終了処理
	}

	/**
	 * EntryPoint
	 * msgを受信した際にここがコールされる
	 * @param message
	 * @throws Exception
	 */
	@Override
	public void onReceive(Object message) throws Exception {

		try {
			// 想定外のmsgを受け取っていないかチェック
			// Objectを送信するならば instanceof でチェックなどで validation
			if (message == null || !(message instanceof String)) {
				// messsage の破棄
				unhandled(message);
				return;
			}

			// 何かビジネス処理

			// SecondActorへ処理を依頼
			ActorRef secondActor = createActor(SecondActor.class, "SendActor-Name");
			// 返信不要の tell() でメッセージ送信
			secondActor.tell("メッセージ", null);

			// 呼び元に終了メッセージ返して1件のJob終了
			sender().tell("DONE", getSelf());

		} catch (Exception e) {
			// error処理

			// 呼び元にメッセージ返して1件のJob終了
			sender().tell("ERROR", getSelf());

			// Actorのエラーを検知出来るようにエラーを投げる
			throw e;
		}
	}

	/**
	 * SecondActorインスタンスの取得
	 * @param clazz
	 * @param actorName
	 * @return
	 */
	protected ActorRef createActor(Class<? extends Actor> clazz, String actorName) {

		// 既存actorあれば返却
		// pathはactorSystmNameから下を指定する(akka://ActorSystem-name/user/xxx)
		// 現在は actorFor() は Deprecation になっているので actorSelection() を使ってください。。
		ActorRef actor = getContext().actorFor("/user/" + actorName);
		if (actor != null && !actor.isTerminated()) {
			return actor;
		}

		// 指定のActorがないので Actor インスタンスを新規作成
		return getContext().system().actorOf(Props.create(clazz), actorName);
	}
}