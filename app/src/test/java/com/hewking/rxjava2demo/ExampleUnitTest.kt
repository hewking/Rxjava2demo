package com.hewking.rxjava2demo

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import org.junit.Test

import org.junit.Assert.*
import org.reactivestreams.Publisher
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.IllegalArgumentException
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    fun testCompose() {
        Flowable.just("rxjava2")
                .compose(object : FlowableTransformer<String, Int> {
                    override fun apply(upstream: Flowable<String>): Publisher<Int> {
                        return upstream.unsubscribeOn(Schedulers.io())
                                .subscribeOn(Schedulers.io())
                                .map {
                                    5
                                }
                                .observeOn(AndroidSchedulers.mainThread())
                    }
                })

    }

    @Test
    fun test1() {
        Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(e: ObservableEmitter<String>) {
                try {
                    var s: String? = null
                    s!!.length
                    e.onNext("hahaha")
                    e.onComplete()
                } catch (ex: Exception) {
//                    e.onError(ex)
                    e.onError(Throwable())
                }
            }
        }).map(Function<String, String> {
            it + 5
        })
                .subscribe({
                    println("onsuccess")
                }, {
                    println(it.printStackTrace())
                })
    }

    /**
     * 测试just 用法
     */
    @Test
    fun testOpJust() {
        val arr = arrayOf("mary", "tom", "ben", "lisa", "ken")
        Observable.fromArray(arr).filter { it.size > 3 }.map { it + "s" }.subscribe(System.out::println)

        val list = arrayListOf("mary", "tom", "ben", "lisa", "ken")
        Observable.just(list).forEach { it -> System.out.println(it + "s") }

        list.stream().filter { it -> it.length > 3 }.map { "$it s" }.forEach(System.out::println)

    }

    @Test
    fun testCreate() {
        Observable.create<String> {
            if (!it.isDisposed) {
                it.onNext("hello")
                it.onNext(" world")
                it.onComplete()
            }
        }.subscribe(System.out::println)
    }

    /**
     * 测试异步Observable，开启单独线程
     */
    @Test
    fun testAsynchronousObservableExample() {
        createAsynchronousObservable()?.subscribe(System.out::println)

    }

    /**
     * 使用先前的例子，发出75个数字，先跳过（skip）10个
     * 然后选取(take)其中5个，并且通过map对每个元素转换
     * 然后迭代输出
     */
    @Test
    fun testOperationOnObservableExample() {
        createAsynchronousObservable()!!.skip(10).take(5)
                .map { "$it coins" }
                .subscribe(System.out::println)
    }

    @Test
    fun testErrorHandle() {
        Observable.create<String> {
            it.onNext("start")
            Thread {
                try {
                    System.out.println("start open ...")
                    it.onNext("start open ...")
                    val stream = URL("https://www.baidu.com").openStream()
                    System.out.println("after url ...")
                    it.onNext("after url")
                    val br = stream.bufferedReader()
                    if (!it.isDisposed) {
                        var text = br.readText()
                        it.onNext(text)
                    }
                    stream.close()
                    br.close()
                    it.onNext("after open ...")
                    if (!it.isDisposed) {
                        it.onComplete()
                    }
                } catch (e: java.lang.Exception) {
                    System.out.println(e)
                    e.printStackTrace()
                    it.onError(e)
                }
            }.start()
        }.subscribe(System.out::println) {
            it.printStackTrace()
            System.out.println("what the fuck")
        }
    }

    private fun createAsynchronousObservable(): Observable<String>? {
        return Observable.create<String> {
            Thread {
                for (i in 0 until 75) {
                    if (!it.isDisposed) {
                        it.onNext("$i")
                    }
                }

                if (!it.isDisposed) {
                    it.onComplete()
                }
            }.start()
        }
    }

    /**
     * 测试all 操作符
     * 检查所有数据项是否符合all后表达式
     */
    @Test
    fun testOpAll() {
        createStringArrayObservable()
                .all {
                    it == "za"
                }.subscribe(Consumer<Boolean> {
                    System.out.println(it)
                })
    }

    private fun createStringArrayObservable() = Observable.fromArray("za", "hui", "shi", "er", "a")

    /**
     * 传入多个源observable,但是只取第一个obsrevable发射数据
     */
    @Test
    fun testOpAmb() {
        Observable.ambArray(createAsynchronousObservable(), createStringArrayObservable())
                .subscribe(System.out::println)
    }

    @Test
    fun testOpDefer() {
        val observable = Observable.defer<String> {
            createAsynchronousObservable()
        }

        observable.subscribe {
            System.out.println(it)
        }

        observable.subscribe(System.out::println)

    }

    @Test
    fun testOpError() {
        Observable.error<Throwable>(IOException(""))
                .subscribe({
                    System.out.print("不会打印吧")
                }, {
                    it.printStackTrace()
                }, {
                    System.out.println("也不会打印")
                })
    }

    /**
     * 抑制链上发生的异常
     */
    @Test
    fun testOpOnErrorResumeNext() {
        val observable = Observable.fromCallable {
            if (Math.random() < 0.5f) {
                throw IllegalArgumentException()
            }
            throw IOException()
        }

        observable.onErrorResumeNext(Function {
            if (it is IllegalArgumentException) {
                Observable.empty()
            } else {
                Observable.error(it)
            }
        }).subscribe({
            System.out.println("nothing")
        },{
            it.printStackTrace()
        },{
            System.out.println("empty")
        })
    }

    @Test
    fun testOpEmpty(){
        Observable.empty<String>().subscribe({
            System.out.println("不会执行")
        },{
            System.out.println("错误不会执行")
        },{
            System.out.println("直接完成")
        })
    }

    /**
     * 不会执行订阅者任何回调
     */
    @Test
    fun testOpNever(){
        Observable.never<String>().subscribe({
            System.out.print("next会执行吗")
        },{
            System.out.print("error 会执行吗")
        },{
            System.out.print("compLete 会执行吗")
        })

    }

    /**
     * 定期发送无限的数字
     */
    @Test
    fun testOpInterval(){
        Observable.interval(1,TimeUnit.SECONDS)
                .onErrorResumeNext(Function {
                    Observable.error(it)
                })
                .subscribe({
                    if (it.rem(5) == 0L) {
                        System.out.println("tick")
                    } else {
                        System.out.println("tock")
                    }
                },{
                    it.printStackTrace()
                },{
                    System.out.println("interval complete")
                })
    }

    @Test
    fun testOpRange(){
        val s = "test range operation now"
        Observable.range(0,s.length- 3)
                .map { "${s[it]} in range"}
                .subscribe {
                    System.out.println(it)
                }
    }

    @Test
    fun testOpGenerate(){
        val start = 1
        val increaseValue = 2
        Observable.generate<Int,Int>(Callable<Int> {
            start
        }, BiFunction<Int, Emitter<Int>,Int> {
            t1, t2 ->
            t2.onNext(t1 + increaseValue)
            t1 + increaseValue
        }).subscribe {
            System.out.println("generate value : $it")
        }
    }

}
