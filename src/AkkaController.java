import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class AkkaController {

	private ActorSystem actorSystem;

	public void execute() throws Exception {

		// ActorSystem起動（パラメータはActorSystemにつける名前です）
		this.actorSystem = ActorSystem.create("ActorSystem-name");

		// ActorSystem起動
		shutdownHook(actorSystem);

		ActorRef actor = null;
		while (true) {

			// Actor の作成（パラメータはActorにつける名前です）
			actor = createActor(FirstActor.class, "actor-name");

			// Actor へメッセージを送り処理を依頼する
			String retMsg = null;
			try {
				// Actor への処理の依頼
				// Actor から返信を待つ ask で msg 送り future を受取る
				// Await.result で future の値を待つ
				retMsg = (String) Await.result(
					Patterns.ask(
						actor
						, "メッセージ内容"
						, 30L * 1000L)
					, Duration.create(
						60L * 1000L
						, TimeUnit.MILLISECONDS)
				);
			} catch (TimeoutException e) {
				// エラー処理

			}
		}
	}

	/**
	 * Actorの作成
	 * @param clazz
	 * @param actorName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected ActorRef createActor(Class<? extends Actor> clazz, String actorName) {

		// Akkaインスタンスがあれば返却
		ActorRef actor = actorSystem.actorFor("/user/" + actorName);
		if (actor != null && !actor.isTerminated()) {
			return actor;
		}

		// インスタンスがないので新規作成
		return actorSystem.actorOf(Props.create(clazz), actorName);
	}

	/**
	 * JVM終了時の処理
	 * @param actorSystem
	 */
	public void shutdownHook(ActorSystem actorSystem) {
		// JVM 終了時に ActorSystem を shutdown する
		Thread t = new Thread(() -> {
			actorSystem.shutdown();
			System.out.println("shutdownHook!!");
			actorSystem.awaitTermination();}
		);

		Runtime.getRuntime().addShutdownHook(t);
	}

}