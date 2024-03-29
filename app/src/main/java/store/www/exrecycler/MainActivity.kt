package store.www.exrecycler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mooveit.library.Fakeit
import kotlinx.android.synthetic.main.activity_main.*
import store.www.exrecycler.model.email
import store.www.exrecycler.model.fakeEmails
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    // variavl que inicia o adapter
    private lateinit var adapter: EmailAdapter
    private var actionMode: ActionMode? = null // instancia a variavel  do action mode do appcompact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Iniciando  a biblioteca do fake it
        Fakeit.init()
        setContentView(R.layout.activity_main)

        adapter = EmailAdapter(fakeEmails())
        recycler_view_main.adapter = adapter
        recycler_view_main.layoutManager = LinearLayoutManager(this)

/*    recycle antiga consumir os dados locais no data class

        recycler_view_main.adapter=EmailAdapter(fakeEmails())
        recycler_view_main.layoutManager=LinearLayoutManager(this)
*/

        // evento de click que adiciona o email fake


        fab.setOnClickListener {
            addEmail()
            recycler_view_main.scrollToPosition(0)

        }


        // atribuindo uma variavel ao
        val helper =
            androidx.recyclerview.widget.ItemTouchHelper(
                // below statement: used at move and sort
                //ItemTouchHelper(androidx.recyclerview.widget.ItemTouchHelper.UP
                // or androidx.recyclerview.widget.ItemTouchHelper.DOWN,
                //androidx.recyclerview.widget.ItemTouchHelper.LEFT)
                ItemTouchHelper(0,
                    androidx.recyclerview.widget.ItemTouchHelper.LEFT)

            )
        helper.attachToRecyclerView(recycler_view_main)
        // referencia o evento que escuta o click na lista do recycle

        adapter.onItemClick = {
            enableActionMode(it)
        }
        adapter.onItemLongClick = {
            enableActionMode(it)
        }
    }
    private fun enableActionMode(position: Int) {
        if (actionMode == null)
            actionMode = startSupportActionMode(object: ActionMode.Callback {


                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    if (item?.itemId == R.id.action_delete) {
                        adapter.deleteEmails()
                        mode?.finish()
                        return true
                    }
                    return false
                }

                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    mode?.menuInflater?.inflate(R.menu.menu_delete, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    adapter.selectedItems.clear()
                    adapter.emails
                        .filter { it.selected }
                        .forEach { it.selected = false }

                    adapter.notifyDataSetChanged()
                    actionMode = null
                }
            })

        adapter.toggleSelection(position)
        val size = adapter.selectedItems.size()
        if (size == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = "$size"
            actionMode?.invalidate()


        }
            }

                // metodo para ordena o intens na recycler view com ajuste de toque


                inner class ItemTouchHelper(dragDirs: Int, swipeDirs: Int) :
                    androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(
                        dragDirs, swipeDirs
                    ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val from = viewHolder.adapterPosition
                        val to = target.adapterPosition

                        Collections.swap(adapter.emails, from, to)
                        adapter.notifyItemMoved(from, to)

                        return true
                    }


                    // remover e reoderna a lista
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        adapter.emails.removeAt(viewHolder.adapterPosition)
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                    }
                }

                // instacia da funcao que adiciona o gerador de emails falsos na recycler view
                fun addEmail() {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).parse(
                        Fakeit.dateTime().dateFormatter()
                    )

                    adapter.emails.add(0,
                        email {
                            stared = false
                            unread = true
                            user = Fakeit.name().firstName()
                            subject = Fakeit.company().name()
                            date = SimpleDateFormat("d MMM", Locale("pt", "BR")).format(sdf)
                            preview = mutableListOf<String>().apply {
                                repeat(10) {
                                    add(Fakeit.lorem().words())
                                }
                            }.joinToString(" ")
                        })

                    adapter.notifyItemInserted(0)
                }
            }

