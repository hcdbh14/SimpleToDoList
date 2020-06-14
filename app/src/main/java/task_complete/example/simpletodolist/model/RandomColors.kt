package task_complete.example.simpletodolist.model

class RandomColors {

    private val colors = listOf(
        0xffEA6153,
        0xffFD982F,
        0xffFAD741,
        0xff3AB37C,
        0xff3D6591,
        0xff9677A7
    )
    private var colorsList =colors.toMutableList()

    fun getRandomColor() : Long {
        if (colorsList.size == 1) {
            colorsList = colors.toMutableList()
        }
        val color = colorsList.random()
        colorsList.remove(color)
        return color
    }
}