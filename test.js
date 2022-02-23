charAmount = 11//Change this to whatever you want
for (var n = 0; n < charAmount; n++) {
    var nc = n
    var result = ""
    var view = charAmount
    while (view > 1) {
        if (nc < (Math.ceil(view / 2))) {
            result += "0"
            view = Math.ceil(view / 2)
        }
        else {
            result += "1"
            nc -= Math.ceil(view / 2)
            view -= Math.ceil(view / 2)
        }
    }
    console.log(n + ": " + result)
}