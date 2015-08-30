/*
 * Copyright (c) 2014-2015 by its authors. Some rights reserved.
 * See the project homepage at: http://www.monifu.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package monifu.reactive.subjects

import monifu.reactive.Ack.Continue
import monifu.reactive.{Observable, Observer}
import scala.util.Success


object PublishSubjectSuite extends BaseSubjectSuite {
  def alreadyTerminatedTest(expectedElems: Seq[Long]) = {
    val s = PublishSubject[Long]()
    Sample(s, 0)
  }

  def continuousStreamingTest(expectedElems: Seq[Long]) = {
    val s = PublishSubject[Long]()
    Some(Sample(s, expectedElems.sum))
  }

  test("issue #50") { implicit s =>
    val p = PublishSubject[Int]()
    var received = 0

    Observable.merge(p).subscribe(new Observer[Int] {
      def onNext(elem: Int) = {
        received += elem
        Continue
      }

      def onError(ex: Throwable) = ()
      def onComplete() = ()
    })

    s.tick() // merge operation happens async

    val f = p.onNext(1)
    assertEquals(f.value, Some(Success(Continue)))
    assertEquals(received, 0)

    s.tick()
    assertEquals(received, 1)
  }
}