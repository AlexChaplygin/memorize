package com.alex.che.memorize.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alex.che.memorize.R
import com.alex.che.memorize.activity.TrainWordsActivity
import com.alex.che.memorize.dto.WordDto
import com.alex.che.memorize.repository.MemorizeDatabase
import org.koin.android.ext.android.inject
import java.util.stream.IntStream
import kotlin.math.min


class CheckWordFragment : Fragment() {

    private val memorizeDatabase: MemorizeDatabase by inject()
    private var word: WordDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        word = arguments?.getSerializable("word", WordDto::class.java)
        if (word == null) {
            Log.i("CheckWordFragment", "Word is null.")
            throw Exception("CheckWordFragment: Word is null.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val checkWordFragmentView = inflater.inflate(R.layout.fragment_check_word, container, false)
        val wordToTrainTv: TextView = checkWordFragmentView.findViewById(R.id.word_to_train_tv)!!
        val wordToCheckEt: EditText = checkWordFragmentView.findViewById(R.id.word_to_check_et)!!
        wordToTrainTv.text = word!!.translation

        val isDifficultChb: CheckBox = checkWordFragmentView.findViewById(R.id.is_difficult_chb)!!
        isDifficultChb.isChecked = word!!.isDifficult
        isDifficultChb.setOnCheckedChangeListener { _, isChecked ->
            memorizeDatabase.wordDao.changeIsDifficult(word!!.id, isChecked)
        }

        val wordBoBheckBacktrackBtn: ImageButton =
            checkWordFragmentView.findViewById(R.id.word_to_check_backtrack_btn)!!
        wordBoBheckBacktrackBtn.setOnClickListener {
            wordToCheckEt.setText(wordToCheckEt.text.toString().dropLast(1))
        }

        val checkWordBtn: Button = checkWordFragmentView.findViewById(R.id.check_word_btn)!!
        checkWordBtn.setOnClickListener {
            checkWord(wordToCheckEt)
        }

        wordToCheckEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == word!!.word) {
                    wordToCheckEt.setBackgroundResource(R.drawable.right_shape_rounded_conteiner)
                    sleep()
                } else {
                    wordToCheckEt.setBackgroundResource(R.drawable.wrong_shape_rounded_conteiner)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        putHints(checkWordFragmentView)

        return checkWordFragmentView
    }

    private fun checkWord(wordToCheckEt: EditText) {
        if (wordToCheckEt.text.toString() == word!!.word) {
            wordToCheckEt.setBackgroundResource(R.drawable.right_shape_rounded_conteiner)
            sleep()
        } else {
            wordToCheckEt.setBackgroundResource(R.drawable.wrong_shape_rounded_conteiner)
        }
    }

    private fun sleep() {
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                back()
            }
        }, 1000)
    }

    private fun putHints(checkWordFragmentView: View) {
        val wordToCheckEt: EditText = checkWordFragmentView.findViewById(R.id.word_to_check_et)!!
        val charsLayout: LinearLayout = checkWordFragmentView.findViewById(R.id.chars_layout)!!
        val context = checkWordFragmentView.context
        val wordCharArray = word!!.word.toCharArray().distinct().toCharArray()
        wordCharArray.shuffle()

        val outValue = TypedValue()
        context.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )

        val count =
            if (wordCharArray.count() % 8 == 0)
                wordCharArray.count() / 8
            else
                (wordCharArray.count() / 8) + 1
        var countChar = 0

        IntStream.range(0, count).forEach {

            val dictionariesHLinearLayout = LinearLayout(context)
            dictionariesHLinearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dictionariesHLinearLayout.gravity = Gravity.CENTER_HORIZONTAL
            dictionariesHLinearLayout.orientation = HORIZONTAL
            dictionariesHLinearLayout.background =
                ContextCompat.getDrawable(context, R.drawable.shape_rounded_conteiners)

            val index = min(8, wordCharArray.count() - countChar)
            IntStream.range(0, index).forEach {

                val char = wordCharArray[countChar]
                val hintCharBtn = Button(context)
                hintCharBtn.text = char.toString()
                hintCharBtn.setTextColor(ColorStateList.valueOf(Color.BLACK))
                hintCharBtn.width = 25
                hintCharBtn.height = 25
                hintCharBtn.textSize = 10F
//                hintCharBtn.foreground =
//                    getDrawable(context, android.R.attr.selectableItemBackground.toInt())
                hintCharBtn.setBackgroundResource(outValue.resourceId)
                hintCharBtn.background =
                    ContextCompat.getDrawable(context, R.drawable.char_shape_button_containers)
                hintCharBtn.setOnClickListener {
                    wordToCheckEt.append(char.toString())
                }

                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    85,
                    85
                )
                params.setMargins(0, 10, 0, 10)
                hintCharBtn.setLayoutParams(params)

                dictionariesHLinearLayout.addView(hintCharBtn)

                countChar++
            }
            charsLayout.addView(dictionariesHLinearLayout)
        }
    }

    private fun back() {
        getParentFragmentManager().popBackStack()
        (activity as TrainWordsActivity).setNewFragment()
    }
}