package io.kabu.frontend.ksp.processor.complex.nested.football2

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.junit.Test


class Football2Test : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
        
            data class Player(
                val name: String,
                val number: Int
            )
            
            data class FootballTeam(
                val name: String,
                val players: List<Player>
            )
            
            open class FootballTeamBuilder {
            
                val players = mutableListOf<Player>()
            
                fun addPlayer(player: Player) {
                    players.add(player)
                }
            }
            
            @GlobalPattern("football team name (builderBlock)")
            fun footballTeam(name: String, builderBlock: FootballTeamBuilder.() -> Unit): FootballTeam {
                val builder = FootballTeamBuilder().apply(builderBlock)
                return FootballTeam(name, builder.players)
            }
            
            @GlobalPattern("player % name / number")
            fun FootballTeamBuilder.player(name: String, number: Int) {
                addPlayer(Player(name, number))
            }

        """,
        sample("""
            val team = football team "Barcelona" {
                player % "Leo Messi" / 10
                player % "Mark Andre Ter Stegen" / 1
            }
            print(team)           
        """) - Termination("FootballTeam(name=Barcelona, players=[Player(name=Leo Messi, number=10), Player(name=Mark Andre Ter Stegen, number=1)])"),
        sample("""
            val team = football team "Barca" {
                player % "Xavi" / 6 
            }
            print(team)           
        """) - Termination("FootballTeam(name=Barca, players=[Player(name=Xavi, number=6)])"),
    )
}
