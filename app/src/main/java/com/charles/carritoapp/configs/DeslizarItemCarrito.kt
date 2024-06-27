package com.charles.carritoapp.configs


import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.charles.carritoapp.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


abstract class DeslizarItemCarrito(context:Context) : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

   val deleteColor = ContextCompat.getColor(context, R.color.deletecolor)
   val archiveColor = ContextCompat.getColor(context,R.color.archivecolor)
    val deleteIcon= R.drawable.ic_baseline_delete_24
    val archiveIcon = R.drawable.ic_baseline_edit_24
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return  false
    }

     override fun onChildDraw(
         c: Canvas,
         recyclerView: RecyclerView,
         viewHolder: RecyclerView.ViewHolder,
         dX: Float,
         dY: Float,
         actionState: Int,
         isCurrentlyActive: Boolean
     ) {

         RecyclerViewSwipeDecorator.Builder(
             c,
             recyclerView,
             viewHolder,
             dX,
             dY,
             actionState,
             isCurrentlyActive
         )
             .addSwipeLeftBackgroundColor(deleteColor)
             .addSwipeLeftActionIcon(deleteIcon)
             .addSwipeRightBackgroundColor(archiveColor)
             .addSwipeRightActionIcon(archiveIcon)
             .create()
             .decorate()
         super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
     }
}