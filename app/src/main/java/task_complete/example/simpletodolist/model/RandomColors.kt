package task_complete.example.simpletodolist.model

class RandomColors {

    private val colors = listOf(
        0xff40E0D0,
        0xff50C878,
        0xff3399cc,

        0xff017351,
        0xff03c383,
        0xffaad962,
        0xfffbbf45,
        0xffef6a32,
        0xffed0345,
        0xffa12a5e,
        0xff710162,
        0xff022c7d

    )
    private var colorsList =colors.toMutableList()

    fun getRandomColor() : Long {
        if (colorsList.size == 2) {
            colorsList = colors.toMutableList()
        }
        val color = colorsList.random()
        colorsList.remove(color)
        return color
    }
}