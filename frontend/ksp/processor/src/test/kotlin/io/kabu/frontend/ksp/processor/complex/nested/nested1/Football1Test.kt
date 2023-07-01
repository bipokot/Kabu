package io.kabu.frontend.ksp.processor.complex.nested.nested1

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.junit.Test

class Football1Test : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
        
            data class Player(
                val name: String,
                val number: Int
            )
            
            data class Trophy(
                val name: String,
                val times: Int
            )
            
            data class FootballTeam(
                val name: String,
                val players: List<Player>,
                val trophies: List<Trophy>
            )
            
            class FootballTeamBuilder {
            
                val players = mutableListOf<Player>()
                val trophies = mutableListOf<Trophy>()
            
                @LocalPattern("name - number")
                fun addPlayer(name: String, number: Int) {
                    players.add(Player(name, number))
                }
            
                @LocalPattern("!number - trophy")
                fun addTrophy(trophy: String, number: Int) {
                    trophies.add(Trophy(trophy, number))
                }
            }
            
            @ContextCreator("footballTeamBuilder")
            fun footballTeamBuilderCreator() = FootballTeamBuilder()
            
            @Pattern("football team name @Extend(context = footballTeamBuilder(), parameter = builder) {}")
            fun footballTeam(name: String, builder: FootballTeamBuilder): FootballTeam {
                return FootballTeam(name, builder.players, builder.trophies)
            }

        """,
        sample("""
            val team = football team "Barcelona" {
                "Leo Messi" - 10
                "Gerard Pique" - 2
        
                !5 - "UEFA League Champions"
                !26 - "Spanish La Liga"
            }
            print(team)            
        """) - Termination("FootballTeam(name=Barcelona, players=[Player(name=Leo Messi, number=10), Player(name=Gerard Pique, number=2)], trophies=[Trophy(name=UEFA League Champions, times=5), Trophy(name=Spanish La Liga, times=26)])")
    )
}
