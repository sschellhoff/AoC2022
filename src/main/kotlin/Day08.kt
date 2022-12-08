fun main() {
    part1(testInput).let { result -> check(result == 21) { "'$result' found but expected '21'" } }
    println(part1(input))
    part2(testInput).let { result -> check(result == 8) { "'$result' found but expected '8'" } }
    println(part2(input))
}

fun part1(problemInput: String): Int {
    val grid = Grid.of(problemInput)
    val numberOfTreesOnTheBorder = grid.perimeter
    val numberOfInnerVisibleTrees = (1 until grid.height - 1).sumOf { y ->
        (1 until grid.width - 1).count { x ->
            isTreeVisible(grid, x, y)
        }
    }

    return numberOfTreesOnTheBorder + numberOfInnerVisibleTrees
}

fun part2(problemInput: String): Int {
    val grid = Grid.of(problemInput)
    return (1 until grid.height - 1).maxOf { y ->
        (1 until grid.width - 1).maxOf { x ->
            viewDistance(grid, x, y)
        }
    }
}

fun isTreeVisible(grid: Grid, x: Int, y: Int): Boolean {
    val treeHeight = grid.get(x, y)
    return !(0 until y).any { grid.get(x, it) >= treeHeight } || !(0 until x).any {
        grid.get(
            it,
            y
        ) >= treeHeight
    } || !(y + 1 until grid.height).any { grid.get(x, it) >= treeHeight } || !(x + 1 until grid.width).any {
        grid.get(
            it,
            y
        ) >= treeHeight
    }
}

fun viewDistance(grid: Grid, x: Int, y: Int): Int {
    val treeHeight = grid.get(x, y)
    val left = (1 until x).reversed().firstOrNull { _x -> grid.get(_x, y) >= treeHeight } ?: 0
    val right = (x + 1 until grid.width).firstOrNull { _x -> grid.get(_x, y) >= treeHeight } ?: (grid.width - 1)
    val up = (1 until y).reversed().firstOrNull { _y -> grid.get(x, _y) >= treeHeight } ?: 0
    val down = (y + 1 until grid.height).firstOrNull { _y -> grid.get(x, _y) >= treeHeight } ?: (grid.height - 1)
    val toLeft = x - left
    val toRight = right - x
    val toUp = y - up
    val toDown = down - y
    return toLeft * toRight * toUp * toDown
}

class Grid(val width: Int, val height: Int) {
    private val data: Array<IntArray> = Array(height) { IntArray(width) }

    private fun set(x: Int, y: Int, value: Int) {
        data[y][x] = value
    }

    val perimeter: Int
        get() = 2 * (width + height) - 4

    fun get(x: Int, y: Int): Int = data[y][x]

    companion object {
        fun of(input: String): Grid {
            val lines = input.lines()
            val grid = Grid(lines.first().length, lines.size)
            lines.forEachIndexed { y, line ->
                line.forEachIndexed { x, value ->
                    grid.set(x, y, value.digitToInt())
                }
            }
            return grid
        }
    }
}

private const val testInput = """30373
25512
65332
33549
35390"""

private const val input =
    """021100121022300121020304244001134204213440331423351253455312304202000142430440402000102100010012001
212112200302012112114404004343323010001555344131524354533222421401044024100314311233132333120002120
001112132022031311343013411004034101221451551452553134332114313511202230442322401330020011133310011
101011102332213111204044323030241245514513514352125432511431552112212230211120021101213101021332122
120201331023003122211414102411241333454454415533145542132412521355224314032201023041331022233003212
111201133202120233040101144053431335432322531225552142451355332245423344140444023441431231311003220
210220301323331223114023214251133322312334113524445351433322242414523254353321044124443201331301120
002220010002313343434033243351213114211241525454566342213254153255525512234354342213032240332302033
110022102004420431301015343132544343554162542463343236455532544153414313554154243110304330112311332
101122133314020410121342231311535243362566353264634235633255233421453134445222411112032343033003020
312122323430244102023214412314455535653626355642333536446663534344514234454432512331012210343110331
102121011122421340411235231142223262366522453366665342434233556652534434234524555514020123034333132
233001100430210405431555214251263246524633556253255335443664225662352533241143545442310012420202002
332233344204032414234154144433344354645565356665552364265454662445624446413334232524502200341111202
223102143031020231344325533663652454434265423436364734665334543225634666435522542524120140041413130
321000340443405412131512466633625336263625455634443645436442326654324336563515445544423044322200011
322101030124113452324444225356545426445466436576746567557453656665263322524342414223555532104311100
200233122013252423324352535666335562473534667534365374536636635764646436452652151424221242242002333
122444442345121153134326544563633437575346544373636766563665656545545445325464645232353353002141313
013022430132321515326235333433335336336637576746435546577576644564773522636636443213434225130413143
132022242245541414134346535246534643675733743644367465565575345766747564666333224445144553402430122
042110243221115344643545454255635536543774335543444335643467543737553776343564265622254114132301334
130012421135112424366645424546345353444657648888648786678354353653576366543325244255225245235002442
400424115353113252334452565774335745655545457555466665665567667756443453735244352362112151452341411
043043241243524256243666547576363566736777457446677656544887588336673543634332643262611143215542234
224121412443523623356566357546665333588546645687758556867887654846553556577554266533423135412312411
040034313321153643333266756775733568445444468755478556477645657665436657365634632255223452553144231
204342111435555654255526563456776665845748644874785558878855577744856736344366345343356222423514010
102002253233256325256375554775746855767546445745648867544456667485487756746663326433534341512154013
220341253535122645222745775343367488648667857685695797774545545565785664747665362424455355144224430
142243344525322423433564534754755468475754959875595788778576687865564475766436657556323465531342444
013241225132422653446664477346858686758586896887576688868957566685845666446637356445226665135233302
401115333115324323333646573486767754575857667758679665978657856846448875853434664356232425554511122
432545442242522255553577446757687656577685569679696685769965899578646687644667667766565324542534411
314421243133225234435633358645845545979699765556955878967656656684444685875434465673546254615334312
123423411426633652564343336778678589685666857776975688898697989698587744447635657432333363345414243
401312414362632327653667454488786756787786858689898687658757855756576785875566434464635334344515142
212212425254354633356747768554568965599965599968678776796887687857595448848837646534652432345221441
235342231533252343753653865647468766979899688776879797688797969868778444744543454773345642653454122
433131515223436246554566665475675668869779797669799966878689996865959744857543443663536554345344425
455312223456233477735366446665798976889689667689678999666767996556655566864655535576556523625231233
451453455555324537447575547857587987587977989887687677779799679777967968544648567665764664342355255
231132532342662457465566865586977577678687697686979686866767997988859978768546734336445464655125422
433243433556462336576647584574576995656967679789778976668679976998965675556667356735343654452235524
511154134255552535634584688754885857777978686799789798776789797957668696667475454375772424655221242
533515326453256647645464488889996886979969987988897977787886777997758587844644744357636343236555544
113334455636464637446577787575989998596687698988897977999786796955668885446757774645774625666313413
441424443533562367374446877655998796979898798989889779898778988668859699877444864563464465536535542
155213253224355635633574467749666956888996887778779879987986988689958559667655744657477226266512434
511534436633425675644374887455867769778968879978798777878976769986976877764765756476746246366424513
222525343345225337335558484559876799996978788879788899978789779779585958455786435646636255462353423
355554162423247675675344885679785855666889897778898877899888867998988668548645764744573425662312514
244444262662445747437348455678886855889888969787999978898679699877779887456678744756657624563653413
143352165456252344465348788568958759899897789978797778789667677865797759457855455644364526334223221
441543245354346776755487648878678655588776698978888999798676976959676767458484836774575536223312354
542511455364544373655658576767855655967967876799788778777798996957695896685785543754355554254311222
552523134432323474365578755847868768977898786779989998987868699866589685654647545633742644342353215
452523232325535743367445748444778859877668699768687967697989869965697854744464474746665435566324552
433124316623344376367576667684578656559769668768878679667798777656685768788576433433342536256323415
334552553536322366576457888564568759867889968768968869987989779887665756844747345475753244434143443
045231455654365674633636858875897565789588699867678897967679656598965567745848776573566222433412315
342344553242644367757545457457879685799978668889896776889678979697757886757585576336544622535114151
025334534564454475733644547554569895796599687688777668867865786698997877475576373767325545355435451
215235555453536264666773484775746866666858957967999769777756887657878547746544367676425644422421315
422555452242646526644766547874785888989588785866889879768996959758764555485646576667326565613444532
431421233136452443437475775484644689878667799987987757997798897678585788876536357753353564452224544
320443525323665353464375567876886688598766785975959775865967666596586785577645336565533343631352231
320343335236445466243635453485667465757797586876599699787695978745775785586667434455424246131512423
322441525315335235473776467764846477759756955699778669655757686546876556735356735744245653114552510
443223215231633354346466664364866675455577787667777769658788958648665757467444554335555335545122232
140443154235366232346776536633886648677677788756995987666854767785758844756745654524262555523431443
103323113424166255555663437645554585454788458776585586597878888756475573373463563234364534354252422
234421131512462653236454776367478465474664655866778687688567745678876334746767744535526241421255411
202213445143322545653547655433644546585575765875645848774868867757655537677574534534552414514250204
043210252544134323446625343443473458876888845877677476848885455466566457334635556335324231551544144
241232432524544622333655374737454543476646847655744577475674886746774564333362632262664514511112120
230030014412322546635253646563435657368548847786678847574767864355743776563533654224521153213111432
233344232313222324656554456756574454444346867687758648756477453676664644762463422246113451515401101
303021224524534134245653233567577436376366444478756447744763376377733767553452264463553233431333132
011422234522552553566335565363653773374564634776744775567574436737444433265343656233143443244320234
012331103314435251565422256453354564373774354746657735333455533433345744556645445635531133320111312
020121120024141513222322423244647764736375367445337744354674763547537454453253236455534424041022021
203341110313124343425542633654543257754747466647757447735736634345643256563233533435335131143430011
012301343320551453151226463266553552536637365754457536333547343776646244432323513114131440203042140
001120001422253323331132546325635646347373343466736577547453773564556544653561254232354400110212131
133112113014231122121522445543236536534236345473766765566632223524532463533212255422423340404023322
321212114131340514551144533334336553643446345564334752555452242634626452456243121333330412111214201
010312310430104424252555255124543456344362645235662466646264454336454354631243313244302111330332023
000023023443202022543511525255335325422542433255344233654646332324444251121413351145404434321430123
221122231031340043315434442144135445245635645366634323626446466363343351334413425251433022022113101
302112013010220000014421525232313564453335455664324662523456444525535441355324111324202404300121120
200220220240231301300245554141343411162264646244256654625336223251311114451515121310330214121100220
232011322124224402034455125412315243355223553225444534323562442512125313131314130333420111022223102
210111332221102412124232223341324152211252553145333635212255424435534524524513100141020320210330022
101103032121130310301403125153525321322215424554144533334151532215513414112444444324030023013312111
111102222102024430344430330133441412455423551522553425333324541124424135524001222303304100133211212
001123320211102003324014022342453112311445112425122242212215141125535432101001241310132112233210110
210022222122313120423343402220011112245525334555255344512311131323245131044444421321103123132122020
001112003032230211110004101211102044114354424515155433555514533343441434122333213130310231012321211"""