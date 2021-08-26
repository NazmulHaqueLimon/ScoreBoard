package com.example.scoreboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.adapters.AdapterViewBindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.scoreboard.R
import com.example.scoreboard.databinding.FragmentNewMatchBinding
import com.example.scoreboard.viewmodels.MatchViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * A fragment to create new matches
 * takes team information and other inputs to start a match
 */
@AndroidEntryPoint
class NewMatchFragment : Fragment() {

    private lateinit var binding: FragmentNewMatchBinding
    private val matchViewModel: MatchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment with the binding object
        binding= FragmentNewMatchBinding.inflate(inflater,container,false).apply {
            viewModel=matchViewModel
            lifecycleOwner=viewLifecycleOwner

            cardView1.setOnClickListener {
                navigateToTeamFragment("A")
            }

            cardView2.setOnClickListener {
                navigateToTeamFragment("B")
            }

            createMatchButton.setOnClickListener {
                matchViewModel.createMatch()
                navigateToScoringFragment()
            }
        }
        // Get input text
         matchViewModel.ground=binding.textInputGround.editText?.text.toString()


        return binding.root
    }




    override fun onResume() {
        super.onResume()
        setDropDowns()
    }



    private fun navigateToTeamFragment(flag: String) {
        matchViewModel.teamFlag.value =flag
        findNavController().navigate(R.id.action_newMatchFragment_to_teamFragment)
    }
    private fun navigateToScoringFragment() {
        val matchId=matchViewModel.match.matchId
        val action = NewMatchFragmentDirections.actionNewMatchFragmentToScoringFragment(matchId)
        findNavController().navigate(action)
    }




    private fun setDropDowns() {
        setOverSelection()
        val teamA =matchViewModel.teamNameA.value.toString()
        val teamB=matchViewModel.teamNameB.value.toString()
        //setting the dropdown options
        val teams = listOf(teamA, teamB)
        val teamsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, teams)
        (binding.tossSelection as? AutoCompleteTextView)?.setAdapter(teamsAdapter)

        binding.tossSelection.setOnItemClickListener{_, _, i, _ ->
            if(teamsAdapter.getItem(i).toString() == teamA){
                matchViewModel.toss =teamA
            }
            else if (teamsAdapter.getItem(i).toString() == teamB){
                matchViewModel.toss =teamB
            }
        }

        (binding.batFirst as? AutoCompleteTextView)?.setAdapter(teamsAdapter)
        binding.batFirst.setOnItemClickListener{_, _, i, _ ->
            if(teamsAdapter.getItem(i).toString() == teamA){
                matchViewModel.teamA.batFirst=true
            }
            else if(teamsAdapter.getItem(i).toString() == teamB){
                matchViewModel.teamB.batFirst=true
            }

        }
    }

    private fun setOverSelection() {
        val formats = listOf("10_Overs", "12_Overs", "15_Overs", "T_20")
        val oversAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, formats)
        (binding.formatSelection as? AutoCompleteTextView)?.setAdapter(oversAdapter)

        binding.formatSelection.setOnItemClickListener { _, _, i, _ ->
            when(oversAdapter.getItem(i).toString()){
                "10_Overs" ->
                    matchViewModel.over = 10
                "12_Overs" ->
                    matchViewModel.over = 12
                "15_Overs" ->
                    matchViewModel.over = 15
                "T_20" ->
                    matchViewModel.over= 20
            }
        }
        //parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
    }



}







