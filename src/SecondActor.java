import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class SecondActor extends UntypedActor{

	/**
	 * インスタンス化される際に1度のみ実行される(costructor)
	 */
	@Override
	public void preStart() {
		// Actor instance の自動終了も可能です
		// 指定秒数Messageが届かなければReceiveTimeoutメッセージが届くように設定 -> instance終了させる
		getContext().setReceiveTimeout(Duration.create(10 * 60, TimeUnit.SECONDS));
	}

	/**
	 * インスタンスが破棄される際に1度のみ実行される(destructor)
	 */
	@Override
	public void postStop() {

	}

	/**
	 * エントリポイント
	 * @param message
	 * @throws Exception
	 */
	@Override
	public void onReceive(Object message) throws Exception {

		try {
			// ReceiveTimeoutならば自分インスタンスを終了
			if (message instanceof ReceiveTimeout) {
				getContext().stop(getSelf());
				unhandled(message);
				return;
			}

			// 必要に応じてmsgチェック

			// 何かビジネスロジック
			System.out.println("hogehoge");

			// 返信不要の tell() でメッセージを受け取ったので 呼び元へメッセージを返す必要はありません

		} catch (Exception e) {
			// 何かエラー処理
			throw e;
		}
	}
}