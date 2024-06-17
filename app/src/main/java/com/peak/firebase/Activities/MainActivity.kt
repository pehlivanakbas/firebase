package com.peak.firebase.Activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.peak.firebase.Adapters.UsersAdapter
import com.peak.firebase.R
import com.peak.firebase.databinding.ActivityMainBinding
import com.peak.firebase.modal.Users

//./gradlew signingReport terminale yazınca sha key çıkar sha1 i kopyala firebasee yapıştır.
// jason file ı alıp app içine yapıştır.
class MainActivity : AppCompatActivity() {

lateinit var mainBinding: ActivityMainBinding

    val database:FirebaseDatabase= FirebaseDatabase.getInstance()
    val myreference:DatabaseReference=database.reference.child("MyUsers")//my user childından çağıracaz
    //child main db altındaki 3 db bulur
        val userList=ArrayList<Users>()
        val imageNameList=ArrayList<String>()
    lateinit var userAdapter:UsersAdapter
    val firebaseStorage:FirebaseStorage=FirebaseStorage.getInstance()
    val storagereferrance:StorageReference=firebaseStorage.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding=ActivityMainBinding.inflate(layoutInflater)
        val view=mainBinding.root
        setContentView(view)
        ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id= userAdapter.getUserId(viewHolder.adapterPosition)
                myreference.child(id).removeValue()
                //delete function

                val imageNAme=userAdapter.getImageName(viewHolder.adapterPosition)

                //profil fotosunu siler
                val imagereferance=storagereferrance.child("images").child(imageNAme)
                imagereferance.delete()


                Toast.makeText(applicationContext,"The user was deleted",Toast.LENGTH_LONG).show()
            }

        })
            .attachToRecyclerView(mainBinding.recyclerView)
        mainBinding.floatingActionButton.setOnClickListener {
            val intent=Intent(this, UserActivity::class.java)
            startActivity(intent)
        }
        retrieveDataFromDatabase()
    }
    fun retrieveDataFromDatabase(){

        myreference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()//tekrar etmesiini önler
                for (eachuser in snapshot.children){
                    val user=eachuser.getValue(Users::class.java)
                //for döngüsü ile her ismi alırız rv ye tanımlarız.

                    if(user!=null){
                        println("userId: ${user.userId}")
                        println("username: ${user.userNAme}")
                        println("userage: ${user.userAge}")
                        println("userEmail: ${user.userEmail}")
                        println("*********************************")
                        userList.add(user)

                        //arraylisti adaptere göndersin

                        userAdapter=UsersAdapter(this@MainActivity,userList)
                        mainBinding.recyclerView.layoutManager=LinearLayoutManager(this@MainActivity)
                        mainBinding.recyclerView.adapter=userAdapter
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_all,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.deleteAll){
            showDialogMessage()
        }
        else if(item.itemId==R.id.signout){
            FirebaseAuth.getInstance().signOut()
            val intent=Intent(this@MainActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialogMessage() {
        val dialogMessage=AlertDialog.Builder(this)
        dialogMessage.setTitle("Delete All Users")
        dialogMessage.setMessage("IF cLick Yes,all user will be deleted,"+
        "if you want to delete specific user, you can swipe the item you want to delete right or left")
dialogMessage.setNegativeButton("Cancel", DialogInterface.OnClickListener{
    dialogInterface, i ->
    dialogInterface.cancel()
})
        dialogMessage.setPositiveButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->

           myreference.addValueEventListener(object :ValueEventListener{
               override fun onDataChange(snapshot: DataSnapshot) {

                   for (eachuser in snapshot.children){
                       val user=eachuser.getValue(Users::class.java)
                       //for döngüsü ile her ismi alırız

                       if(user!=null){
            imageNameList.add(user.imageName)

                       }

                   }

               }

               override fun onCancelled(error: DatabaseError) {
                   TODO("Not yet implemented")
               }
           })



            myreference.removeValue().addOnCompleteListener { task ->
                if(task.isSuccessful){

                    for (imageName in imageNameList){
                        val imagereferance=storagereferrance.child("images").child(imageName)


                        imagereferance.delete()
                    }
                    userAdapter.notifyDataSetChanged()
                    Toast.makeText(applicationContext,"All users were deleted",Toast.LENGTH_LONG).show()
                }
            }
        })
        dialogMessage.create().show()

    }
}