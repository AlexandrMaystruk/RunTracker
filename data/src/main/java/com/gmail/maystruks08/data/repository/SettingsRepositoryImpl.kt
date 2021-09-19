package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.QrCodeGenerator
import com.gmail.maystruks08.data.XLSParser
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.mappers.toFirestoreRunner
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.data.remote.pojo.DistanceCheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.DistancePojo
import com.gmail.maystruks08.data.remote.pojo.RacePojo
import com.gmail.maystruks08.domain.entities.DistanceType
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class SettingsRepositoryImpl @Inject constructor(
    private val preferences: ConfigPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val xlsParser: XLSParser,
    private val qrCodeGenerator: QrCodeGenerator,
    private val api: Api
) : SettingsRepository {


    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun getAdminUserIds(): List<String> {
        return emptyList()
    }

    override fun clearCurrentSelectedRace() {
        preferences.clearRaceId()
        preferences.clearRaceName()
    }

    override suspend fun exportRunnerDataFromXlsToRemote() {
        val authorId = "maystruks08@gmail.com"
        val raceName = "100_for_24_2021"
        val raceId = getUUID(raceName)
        val uniqueId = raceId
        val runners = xlsParser.readExcelFileFromAssets(raceId, "runners_$uniqueId", "100_24_run.xls")
        val iron = xlsParser.readExcelFileFromAssets(raceId, "iron_$uniqueId", "100_24_iron_run.xls")
        val relayRace = xlsParser.readExcelFileFromAssets(raceId, "relay_race_$uniqueId", "100_24_estaf.xls")
        val teams = xlsParser.readExcelFileFromAssets(raceId, "teams_$uniqueId", "100_24_run_group.xls")

        val distances = listOf(
            DistancePojo(
                id = "runners_$uniqueId",
                raceId = raceId,
                name = "Бегуны",
                type = DistanceType.MARATHON.name,
                authorId = authorId,
                dateOfStart = null,
                runnerIds = runners.map { it.number }
            ),
            DistancePojo(
                id = "iron_$uniqueId",
                raceId = raceId,
                name = "Железные",
                type = DistanceType.MARATHON.name,
                authorId = authorId,
                dateOfStart = null,
                runnerIds = iron.map { it.number }
            ),
            DistancePojo(
                id = "relay_race_$uniqueId",
                raceId = raceId,
                type = DistanceType.REPLAY.name,
                name = "Эстафета",
                authorId = authorId,
                dateOfStart = null,
                runnerIds = relayRace.map { it.number }
            ),
            DistancePojo(
                id = "teams_$uniqueId",
                raceId = raceId,
                type = DistanceType.TEAM.name,
                name = "Команды",
                authorId = authorId,
                dateOfStart = null,
                runnerIds = teams.map { it.number }
            ),
        )

        distances.forEach {
            val checkpoints = getCheckpoints(it.name, it.id)
            api.saveDistanceCheckpoints(it.id, checkpoints)
        }

        val race = RacePojo(
            id = raceId,
            name = "Одесская Сотка 2021",
            dateCreation = Date(),
            registrationIsOpen = true,
            authorId = authorId,
            adminListIds = emptyList(),
            distanceListIds = distances.map { it.id }
        )
        api.saveRace(race)
        distances.forEach { api.saveDistance(it) }

        exportRunners(runners)
        exportRunners(iron)
        exportRunners(relayRace)
        exportRunners(teams)
    }

    private fun getCheckpoints(distanceName: String, distanceId: String): List<DistanceCheckpointPojo> {
        return when (distanceName) {
            "Бегуны" -> getNormalCheckpoints(distanceId)
            "Железные" -> getIronCheckpoints(distanceId)
            "Эстафета" -> getEstafCheckpoints(distanceId)
            "Команды" -> getTeamCheckpoints(distanceId)
            else -> throw RuntimeException()
        }
    }

    private fun getEstafCheckpoints(distanceId: String) = listOf(
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "S",
            position = 0
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "15",
            position = 1
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "40",
            position = 2
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "52",
            position = 3
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "70",
            position = 4
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "80",
            position = 5
        ),

        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "90",
            position = 6
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "F",
            position = 7
        ),
    )

    private fun getTeamCheckpoints(distanceId: String) = listOf(
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "S",
            position = 0
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "15",
            position = 1
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "40",
            position = 2
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "52",
            position = 3
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "70",
            position = 4
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "80",
            position = 5
        ),

        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "90",
            position = 6
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "F",
            position = 7
        ),
    )

    private fun getNormalCheckpoints(distanceId: String) = listOf(
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "S",
            position = 0
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "15",
            position = 1
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "40",
            position = 2
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "52",
            position = 3
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "70",
            position = 4
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "80",
            position = 5
        ),

        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "90",
            position = 6
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "F",
            position = 7
        ),
    )

    private fun getIronCheckpoints(distanceId: String) = listOf(
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "S",
            position = 0
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "7.5",
            position = 1
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "15",
            position = 1
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "40",
            position = 2
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "52",
            position = 3
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "70",
            position = 4
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "80",
            position = 5
        ),

        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "90",
            position = 6
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "F",
            position = 7
        )
    )

    private suspend fun exportRunners(runners: List<Runner>) {
        Timber.i("Parse count ${runners.size}")
        var count = 0
        runners.forEach {
            api.saveRunner(it.toFirestoreRunner())
            count++
        }
        Timber.i("Saved count $count")
    }

    private fun getUUID(name: String): String {
        return "${name.replace(" ", "_")}_${UUID.randomUUID().toString().replace("-", "_")}"

    }


    override suspend fun generateQrCodes() {
        val list = listOf(
            "777",
            "020",
            "021",
            "023",
            "024",
            "111",
            "025",
            "999",
            "026",
            "027",
            "221",
            "888",
            "028",
            "029",
            "555",
            "030",
            "450",
            "757",
            "031",
            "032",
            "033",
            "034",
            "035",
            "038",
            "039",
            "040",
            "600006/041",
            "042",
            "043",
            "044",
            "003",
            "045",
            "046",
            "505",
            "047",
            "048",
            "049",
            "098",
            "050",
            "051",
            "052",
            "053",
            "054",
            "055",
            "056",
            "313",
            "057",
            "058",
            "059",
            "575",
            "060",
            "061",
            "062",
            "063",
            "064",
            "065",
            "067",
            "068",
            "069",
            "070",
            "071",
            "166",
            "096",
            "072",
            "073",
            "074",
            "075",
            "303",
            "079",
            "080",
            "081",
            "123",
            "083",
            "084",
            "085",
            "086",
            "087",
            "088",
            "089",
            "090",
            "222",
            "091",
            "092",
            "093",
            "094",
            "095",
            "097",
            "099",
            "101",
            "102",
            "103",
            "104",
            "036",
            "105",
            "106",
            "107",
            "109",
            "110",
            "112",
            "113",
            "114",
            "115",
            "116",
            "117",
            "118",
            "119",
            "120",
            "121",
            "124",
            "125",
            "126",
            "127",
            "128",
            "129",
            "131",
            "132",
            "133",
            "134",
            "135",
            "137",
            "138",
            "139",
            "140",
            "141",
            "143",
            "145",
            "146",
            "147",
            "148",
            "149",
            "150",
            "151",
            "152",
            "153",
            "1.61",
            "154",
            "992",
            "157",
            "158",
            "290",
            "159",
            "160",
            "161",
            "162",
            "163",
            "164",
            "165",
            "005",
            "167",
            "168",
            "169",
            "170",
            "171",
            "174",
            "175",
            "176",
            "000",
            "177",
            "178",
            "001",
            "179",
            "180",
            "444",
            "181",
            "182",
            "183",
            "184",
            "185",
            "186",
            "187",
            "201",
            "202",
            "203",
            "204",
            "205",
            "206",
            "207",
            "208",
            "209",
            "210",
            "211",
            "212",
            "213",
            "214",
            "215",
            "216",
            "217",
            "218",
            "219",
            "220",
            "223",
            "224",
            "225",
            "226",
            "227",
            "228",
            "229",
            "230",
            "231",
            "232",
            "233",
            "234",
            "235",
            "236",
            "237",
            "238",
            "239",
            "240",
            "241",
            "242",
            "243",
            "244",
            "245",
            "246",
            "247",
            "248",
            "249",
            "250",
            "500",
            "501",
            "502",
            "503",
            "509",
            "108",
            "511",
            "512",
            "513",
            "333",
            "514",
            "515",
            "516",
            "518",
            "519",
            "520",
            "521",
            "522",
            "523",
            "524",
            "525",
            "530",
            "531",
            "532",
            "533",
            "534",
            "535",
            "536",
            "537",
            "538",
            "539",
            "540",
            "541",
            "542",
            "543",
            "544",
            "545",
            "546",
            "547",
            "548",
            "549",
            "550",
            "551",
            "552",
            "553",
            "554",
            "556",
            "557",
            "558",
            "559",
            "560",
            "50000",
            "55555",
            "50001",
            "50002",
            "50003",
            "50004",
            "50005",
            "50006",
            "50007",
            "50008",
            "50009",
            "50010",
            "50016",
            "81274",
            "50017",
            "50018",
            "11111",
            "50019",
            "50020",
            "50021",
            "50022",
            "50023",
            "50011",
            "50012",
            "50013",
            "50014",
            "50015",
            "802",
            "803",
            "804",
            "805",
            "806",
            "807",
            "808",
            "809",
            "810",
            "811",
            "812",
            "813",
            "814",
            "815",
            "816",
            "817",
            "818",
            "819",
            "820",
            "800",
            "821",
            "822",
            "191",
            "362",
            "823",
            "824",
            "830",
            "831",
            "832",
            "833",
            "834",
            "835",
            "836",
            "837",
            "838",
            "839",
            "840",
            "841",
            "842",
            "843",
            "844",
            "845",
            "846",
            "847",
            "848",
            "849",
            "850",
            "851",
            "852",
            "853",
            "854",
            "855",
            "856",
            "857",
            "858",
            "859",
            "860"
        )
        qrCodeGenerator.generateQRCodes(list)
    }
}