package com.example.simpletodolist.model

class RandomColors {

    private var colors = arrayOf(0xfffec6262,0xe1d276,0xff99ea86,0xff83d6b7,
        0xffe4a8f9,0xffcaf1de,0xffccffcc,
        0xff00bcd4,
        

        0xff009688,0xff4caf50,0xff8bc34a,0xffcddc39,
        0xffffeb3b,0xffffc107,0xffff9800,0xffff5722,
        0xff795548,0xff9e9e9e,0xff607d8b,0xff333333)


    fun getRandomColor() : Long {
        return colors.asList().shuffled()[0]
    }
}