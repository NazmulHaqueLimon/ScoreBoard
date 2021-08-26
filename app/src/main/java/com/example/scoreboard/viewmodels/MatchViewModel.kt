package com.example.scoreboard.viewmodels

import androidx.lifecycle.*
import com.example.scoreboard.data.objects.Match
import com.example.scoreboard.data.objects.Player
import com.example.scoreboard.data.objects.Team
import com.example.scoreboard.data.objects.TeamPlayers
import com.example.scoreboard.data.repositories.NewMatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject internal constructor(

    private val savedStateHandle: SavedStateHandle,
    private val repository : NewMatchRepository

): ViewModel() {

    val teamFlag = MutableLiveData<String>()

    val teamNameA = MutableLiveData<String>()
    val teamA : Team = Team(name=teamNameA.value.toString())

    val teamNameB = MutableLiveData<String>()
    val teamB : Team = Team(name=teamNameB.value.toString())

    var over : Int =10
    var toss :String ="teamA"
    var ground :String ="Local cricket ground"


    private val _teamAplayers = MutableLiveData<List<Player>>()
    private val teamAplayers :LiveData<List<Player>> =_teamAplayers


    private val _teamBplayers = MutableLiveData<List<Player>>()
    private val teamBplayers :LiveData<List<Player>> =_teamAplayers

    val match : Match =Match(
        teamA_id= teamA.teamId,
        teamB_id = teamB.teamId,
        format = over,
        ground = ground
        )

    /**
     * handle newMatchFragments textInputs and save in live data
     *
     */
    fun collectTeamAndPlayers(tName:String ,pList:List<Player>){
        if (teamFlag.value.equals("A")){
            teamNameA.value = tName
            _teamAplayers.value = pList
        }
        else if (teamFlag.value.equals("B")){
            teamNameB.value = tName
            _teamBplayers.value = pList
        }

    }

    /**
     * startMatch()-->save players,save teams,save match
     */
    fun createMatch() {
        savePlayers()
        saveTeams()
        saveTeamPlayers()
        saveMatch()
    }

    private fun saveMatch() {
        viewModelScope.launch {
            repository.saveMatch(match)
        }

    }
    private fun saveTeamPlayers(){
        viewModelScope.launch {
            teamAplayers.value?.map { player ->
                val teamPlayer=TeamPlayers(teamA.teamId,player.id)
                repository.saveTeamPlayers(teamPlayer)
            }
            teamBplayers.value?.map {player->
                val teamPlayer=TeamPlayers(teamB.teamId,player.id)
                repository.saveTeamPlayers(teamPlayer)
            }
        }

    }
    /**
     * fun saveTeamWithPlayers(){
     *
     * }
     * */

    private fun saveTeams() {
        viewModelScope.launch {
            repository.saveTeam(teamA)
            repository.saveTeam(teamB)
        }
    }

    private fun savePlayers() {
        viewModelScope.launch {
            teamAplayers.value?.let { repository.savePlayers(it) }
            teamBplayers.value?.let { repository.savePlayers(it) }
        }
    }


}