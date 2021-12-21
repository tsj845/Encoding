class BitTree ():
    """
    tree that maps bits to characters
    """
    def __init__ (self, data):
        self.tree = data
        if (type(data) == str):
            self.tree = self._construct(data)
    def _construct (self, data):
        f = list(data)
        while len(f) > 2:
            b = []
            for i in range(len(f)):
                if (i % 2 == 0):
                    continue
                b.append([f[i-1],f[i]])
            if (f[-1] not in b[-1]):
                b.append(f[-1])
            f = b
        return f
    def __getitem__ (self, path):
        s = self.tree
        for c in path:
            s = s[int(c)]
        return s
    def __setitem__ (self, path, value):
        s = self.tree
        for c in path:
            if (type(s[int(c)]) == str):
                break
            s = s[int(c)]
        s[int(path[-1])] = value

class Encoder ():
    """
    does encoding and decoding
    """
    def __init__ (self):
        self.protocols = {
            "bintree" : {
                "id" : 0xadfc,
                "versions" : [0x1498],
                "data" : {
                    0x1498 : "abcdefgh"
                }
            },
            "hextable" : {
                "id" : 0xa7e4,
                "versions" : [0x2048],
                "data" : {
                    0x2048 : " abcdefghijklmnopqrstuvwxyz"
                }
            }
        }
    def _compress (self, data):
        trailing = data[len(data)-(len(data)%4):]
        data = data[:len(data)-(len(data)%4)]
        f = ""
        for i in range(0, len(data), 4):
            print(data[i:i+4])
            f += hex(int(data[i:i+4], base=2))[2:]
        f += ";" + trailing
        return f
    def _expand (self, data):
        trailing = data[data.index(";")+1:]
        data = data[:data.index(";")]
        f = ""
        for c in data:
            f += bin(int(c, base=16))[2:].rjust(4, "0")
        f += trailing
        return f
    def _encode_bt (self):
        pass
    def encode (self, protocol, version, data):
        pid = self.protocols[protocol]["id"]
        vid = self.protocols[protocol]["versions"][version]
        edata = self.protocols[protocol]["data"][vid]
        if (pid == 0xadfc):
            self._encode_bt(pid, vid, edata)
        elif (pid == 0xa7e4):
            self._encode_xt(pid, vid, edata)
    def decode (self, protocol, version, data):
        pid = self.protocols[protocol]["id"]
        vid = self.protocols[protocol]["versions"][version]
        edata = self.protocols[protocol]["data"][vid]
        if (pid == 0xadfc):
            self._decode_bt(pid, vid, edata)
        elif (pid == 0xa7e4):
            self._decode_xt(pid, vid, edata)

e = Encoder()
comp = e._compress("00111010")
print(comp, e._expand(comp))