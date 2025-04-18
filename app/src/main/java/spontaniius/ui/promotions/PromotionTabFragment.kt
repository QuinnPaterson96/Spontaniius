package spontaniius.ui.promotions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton

import androidx.fragment.app.Fragment
import com.spontaniius.R
import spontaniius.ui.create_event.CreateEventFragment

class PromotionTabFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var PromoIconView: ImageButton
    private lateinit var gender: String
    private val radius1 = 100
    private val radius2 = 500
    private val radius3 = 1000
    companion object {
        fun newInstance() = PromotionTabFragment()
    }

    private lateinit var viewModel: PromotionTabViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_promotions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
//        Do nothing
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }
}
