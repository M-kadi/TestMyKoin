package com.example.testmykoin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testmykoin.dataModule.MyViewModel
import com.example.testmykoin.room.UserRepository
import com.example.testmykoin.sqlite.DbHelper
import com.example.testmykoin.sqlite.User
import com.example.testmykoin.userList.UserListAdapter
import com.example.testmykoin.userList.UsersViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.input_user.view.*
import kotlinx.android.synthetic.main.list_user.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.compat.ViewModelCompat.viewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val car2 : Car2 by inject()
    private val car3 : Car3 by inject()

    val mySharedPreferences : MySharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("Koin_","" + car2.maker())
        Log.i("Koin_","" + car3.maker())

        mySharedPreferences.putData("mmm", 99)
        val value = mySharedPreferences.getData("mmm")
        Log.i("ooooooo", "" + value)

//        testViewModel.test()

//        testSqlite.test()

        testRoom.test()
    }

    val testViewModel = TestViewModel()
    inner class TestViewModel{
        private val myViewModel : MyViewModel by viewModel()
        fun test(){


            lifecycle.addObserver(myViewModel)
//        viewModel.showTextDataNotifier.observe(this, textDataObserver)
//        btn_fetch.setOnClickListener { viewModel.fetchValue() }
            ///
            myViewModel.getCounter().observe(this@MainActivity, Observer {
                txt1122.setText("Count is "+it)
            })

            btn22.setOnClickListener {
                myViewModel.addCounter()
            }
        }
    }

    val testSqlite = TestSqlite()
    inner class TestSqlite{
        private val dbHelper : DbHelper by inject()

        fun test(){
//            btnSqlite.setOnClickListener {
                val id = dbHelper.insertUser(User("mkadi"))
                val value = dbHelper.getUser(id)
                Log.i("ggggg","" + value)
                Toast.makeText(this@MainActivity, "Sqlie $value" , Toast.LENGTH_SHORT).show()
//            }
        }

    }

    val testRoom = TestRoom()
    inner class TestRoom{
        fun test(){
//            test_show_room()
//            test_insert_room()

            testRoomUserList()
        }

        private val usersViewModel : UsersViewModel by viewModel()
        private fun testRoomUserList(){



            btnAdd.setOnClickListener {
                val btnsheet = layoutInflater.inflate(R.layout.input_user, null)
                val dialog = BottomSheetDialog(this@MainActivity)
                dialog.setContentView(btnsheet)

                fun onSave(){
                    GlobalScope.launch {
                        val user = btnsheet.editUser.text.toString()
                        when {
                            user.isBlank() ->
                                this@MainActivity.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(this@MainActivity, "Set User Name", Toast.LENGTH_SHORT)
                                        .show()
                                })
                            else -> {
                                usersViewModel.insert(user)
                                //or => //userDao.insert(com.example.koin.room.User(user))
                            }
                        }
                    }
                }

                btnsheet.btnSave.setOnClickListener {
                    onSave()
                    btnsheet.btnSave.hideKeyboard()
                    dialog.dismiss()
                }
                dialog.show()


                btnsheet.editUser.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                    val hasEnterOrGo = keyCode == KeyEvent.KEYCODE_ENTER || keyCode == EditorInfo.IME_ACTION_GO
                    return@OnKeyListener when (event.action == KeyEvent.ACTION_DOWN && hasEnterOrGo) {
                        true -> btnsheet.btnSave.callOnClick().let { true }
                        false -> false
                    }
                })


            }


            val adapter = UserListAdapter(this@MainActivity).apply {
                registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onChanged() {
                        super.onChanged()
                        empty_view.isVisible = this@apply.itemCount == 0
                    }
                })
            }

            recyclerview.adapter = adapter
            recyclerview.layoutManager = LinearLayoutManager(this@MainActivity)

            usersViewModel.allUsers.observe(this@MainActivity) { users ->
                adapter.setWords(users)
            }
        }


        // room
        private val userRepository : UserRepository by inject()
        private fun test_insert_room(){
//            btn_insert_room1.setOnClickListener {
//      val wordDao = WordRoomDatabase.getDatabase(requireContext()).wordDao()
            GlobalScope.launch {
//            userDao.run {
//
//            }
                userRepository.insert("hello10000")
                userRepository.insert("hello20000")
                userRepository.insert("hello30000")

            }
//            }
        }

        private fun test_show_room(){
            val allUsers =
                userRepository.allUsers.flowOn(Dispatchers.Main)
                    .asLiveData(context = GlobalScope.coroutineContext)

//            btn_show_room1.setOnClickListener {
            allUsers
                .observe(this@MainActivity, Observer {
                    // foo is still nullable
                    // get value of LiveData : one times : after MainActivity onCreate
                    it.forEach { Log.i("fff", "nullable " +  it.usr_name) }
                })

            allUsers
                .nonNull()
                .observe(this@MainActivity, {
                    // Now foo is non-null
                    // get value of LiveData : always
                    it.forEach { Log.i("fff", "non-null " +  it.usr_name) }

                    Log.i("fff", "non-null size " +  it.size)
                })
//            }
        }
    }


}

// room
// get value of LiveData : always
class NonNullMediatorLiveData<T> : MediatorLiveData<T>()
fun <T> LiveData<T>.nonNull(): NonNullMediatorLiveData<T> {
    val mediator: NonNullMediatorLiveData<T> = NonNullMediatorLiveData()
    mediator.addSource(this, Observer { it?.let { mediator.value = it } })
    return mediator
}
fun <T> NonNullMediatorLiveData<T>.observe(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer {
        it?.let(observer)
    })
}

fun TextView.hideKeyboard() {
    clearFocus()
    getInputMethodManager()?.hideSoftInputFromWindow(windowToken, 0)
}

private fun TextView.getInputMethodManager() =
    ContextCompat.getSystemService(context, InputMethodManager::class.java)
