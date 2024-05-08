import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jyotsna.expensesfusion.Expense
import com.jyotsna.expensesfusion.R

class ExpenseAdapter(private val expenses: List<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val currentExpense = expenses[position]
        holder.textViewExpenseName.text = currentExpense.name
        holder.textViewExpenseAmount.text = "$ ${currentExpense.amount}"
        holder.textViewTaxDeductible.text =
            if (currentExpense.isTaxDeductible) "Tax Deductible" else "Not Tax Deductible"
    }

    override fun getItemCount() = expenses.size

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewExpenseName: TextView = itemView.findViewById(R.id.textViewExpenseName)
        val textViewExpenseAmount: TextView = itemView.findViewById(R.id.textViewExpenseAmount)
        val textViewTaxDeductible: TextView = itemView.findViewById(R.id.textViewTaxDeductible)
    }
}
