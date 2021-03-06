from numpy import floor
from sys import argv as script_args

class BitTree ():
    """
    tree that maps bits to characters
    """
    def __init__ (self, data, swap_policy=2):
        # use this to configure how final swaps are done
        self._swap_policy = swap_policy
        self.tree = data
        self._contains = self._getcontaining(data)
        if (type(data) == str):
            self.tree = self._construct(data)
            self._swapbottom(data)
            # print(self.tree)
    def __contains__ (self, value):
        return value in self._contains
    def has (self, char):
        return char in self._contains
    def _getcontaining (self, data):
        if (type(data) == str):
            return data
        f = ""
        for i in range(len(data)):
            f += self._getcontaining(data[i])
        return f
    def _fit (self, tree, char):
        for i in range(len(tree)):
            if (type(tree[i]) != str):
                found, ret = self._fit(tree[i], char)
                if (found):
                    return True, str(i)+ret
            else:
                if (tree[i] == char):
                    return True, str(i)
        return False, ""
    def find (self, char):
        return self._fit(self.tree, char)[1]
    def _swap (self, l, i1=0, i2=1):
        l = l.copy()
        h = l[i1]
        l[i1] = l[i2]
        l[i2] = h
        return l
    def _getdepth (self, lst, depth=0):
        depths = []
        for i in range(len(lst)):
            if (type(lst[i]) != str):
                depths.append(self._getdepth(lst[i], depth+1))
        return max(depths) if len(depths) > 0 else depth
    def _swaplayers (self, lst, pi=0):
        for i in range(len(lst)):
            if (type(lst[i]) != str and self._getdepth(lst[i]) > 2 and pi % 2 == 0):
                lst[i] = self._swaplayers(lst[i], i)
        return self._swap(lst)
    def _swapbottom (self, org):
        swapped = ""
        xv = 1
        for i in range(0, len(org), 2):
            if (org[i] in swapped and self._swap_policy < 3):
                continue
            ni = (i + 1) % len(org)
            if (self._swap_policy == 0):
                ni = (i + ord(org[i])) % len(org)
            elif (self._swap_policy == 1):
                ni = (i + len(org) // 2) % len(org)
            elif (self._swap_policy == 2):
                ni = (i + round(floor(len(org)/10))-5) % len(org)
            elif (self._swap_policy == 3):
                ni = (i + (round(floor((len(org)/12.5)*max(0.1, i*(len(org)/100))))+2) * xv)
                xv = - xv
                while ni < 0:
                    ni += len(org)
                while ni >= len(org):
                    ni -= len(org)
            swapped += org[i]
            swapped += org[ni]
            # print(org[i], org[ni], "SWAP")
            p1 = self.find(org[i])
            p2 = self.find(org[ni])
            h = org[ni]
            self[p2] = org[i]
            self[p1] = h
    def _construct (self, data):
        f = list(data)
        # print(f)
        while len(f) > 2:
            b = []
            for i in range(2, len(f), 3):
                b.append([f[i-2], [f[i-1], f[i]]])
            if (len(f) % 3 > 0):
                b.append([*f[len(f)-(len(f)%3):]] if len(f)%3 > 1 else f[-1])
            f = b
        while len(f) == 1:
            f = f[0]
        f = self._swaplayers(f)
        # print(f)
        return f
    def __getitem__ (self, path):
        s = self.tree
        if (type(path) == int):
            return s[path]
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
    def __repr__ (self):
        return self.tree.__repr__()

class Encoder ():
    """
    does encoding and decoding
    """
    def __init__ (self):
        self.protocols = {
            0xadfc : {
                "versions" : [0x1498, 0xbc0e],
                "data" : {
                    0x1498 : {"tree":"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? ???","nib":2,"nibd":2},
                    0xbc0e : {"tree":"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? ???\n\t\\\"'{}[];","nib":2,"nibd":2}
                }
            },
            0xa7e4 : {
                "versions" : [0x2048],
                "data" : {
                    0x2048 : {"table":" abcdefghijklmnopqrstuvwxyz0123456789,.!?ABCDEFGHIJKLMNOPQRSTUVWXYZ???", "width":7}
                }
            }
        }
        self.namemap = {"bittree":0xadfc, "hextables":0xa7e4}
    def _compress (self, data):
        # trailing = data[len(data)-(len(data)%4):]
        data = data[:len(data)-(len(data)%4)]
        f = ""
        for i in range(0, len(data), 4):
            f += hex(int(data[i:i+4], base=2))[2:]
        return f
    def _expand (self, data):
        f = ""
        for c in data:
            f += bin(int(c, base=16))[2:].rjust(4, "0")
        return f
    def _generate_header (self, pid, vid, encoded, compress=True, usenib=False, pnib=""):
        pid = bin(pid)[2:].rjust(16, "0")
        vid = bin(vid)[2:].rjust(16, "0")
        aln = (16-(len(encoded)%16)) % 16
        encoded = encoded.ljust(len(encoded)+aln, "0")
        aln = bin(aln)[2:].rjust(4 if usenib else 8, "0")
        nib = bin(pnib)[2:].rjust(4, "0") if usenib else ""
        fdata = pid+vid+nib+aln+encoded
        return self._compress(fdata) if compress else fdata
    def _unpack_data (self, cdata):
        b = self._expand(cdata)
        pid = int(b[:16],base=2)
        vid = int(b[16:32],base=2)
        nib = int(b[32:36],base=2)
        tbits = int(b[36:40],base=2)
        data = b[40:-tbits]
        return pid, vid, data, nib
    def _encode_bt (self, pid, vid, edata, data):
        tree = BitTree(edata["tree"], edata["nib"])
        encoded = ""
        for c in data:
            p = tree.find(c)
            print(p, c)
            encoded += p
        header = self._generate_header(pid, vid, encoded, usenib=True, pnib=edata["nib"])
        return header
    def _decode_bt (self, edata, data, nib):
        tree = BitTree(edata["tree"], nib)
        f = ""
        work = tree
        for c in data:
            work = work[int(c)]
            if (type(work) == str):
                f += work
                work = tree
        return f
    def _encode_xt (self, pid, vid, edata, data):
        table = edata["table"]
        width = edata["width"]
        encoded = ""
        for c in data:
            encoded += bin(table.index(c))[2:].rjust(width, "0")
        header = self._generate_header(pid, vid, encoded)
        return header
    def _decode_xt (self, edata, data):
        table = edata["table"]
        width = edata["width"]
        f = ""
        for i in range(0, len(data), width):
            f += table[int(data[i:i+width], base=2)]
        return f
    def encode (self, protocol, version, data):
        pid = self.namemap[protocol] if type(protocol) == str else protocol
        vid = self.protocols[pid]["versions"][version]
        edata = self.protocols[pid]["data"][vid]
        res = "NULL"
        if (pid == 0xadfc):
            res = self._encode_bt(pid, vid, edata, data)
        elif (pid == 0xa7e4):
            res = self._encode_xt(pid, vid, edata, data)
        return res
    def decode (self, data):
        pid, vid, data, nib = self._unpack_data(data)
        edata = self.protocols[pid]["data"][vid]
        res = "NULL"
        if (pid == 0xadfc):
            res = self._decode_bt(edata, data, nib)
        elif (pid == 0xa7e4):
            res = self._decode_xt(edata, data)
        return res

e = Encoder()
comp = e._compress("00111010")
# print(comp, e._expand(comp))
x = e.protocols[0xadfc]["data"][0x1498]["tree"]
# x = e._generate_header(0xa7e4, 0x2048, "101010010101")

class Interface ():
    def __init__ (self):
        self.conf = {
            "PROT" : 0xadfc,
            "VERS" : 0,
            "NIB" : 2,
        }
        self.hd = {
            "topics" : "config, encode, decode",
            "config" : "type \"get\" [name] to get config value\ntype \"set\" [name] [value] to set config value",
            "encode" : "type \"encode\" to enter the encoder, type text to encode and it will be encoded using the settings set in the \"config\" interface, type \"\\QUIT\" when encoding \"QUIT\"",
            "decode" : "type \"decode\" to enter the decoder, type encoded values to decode them, type \"QUIT\" to return to the main interface, config settings don't matter"
        }
        self.interface()
    def help (self):
        print("type \"QUIT\" to quit, and \"topics\" for a list of help topics")
        while True:
            inp = input("help> ")
            if (inp == "QUIT"):
                break
            if (inp in self.hd):
                print(self.hd[inp])
    def _parse (self, value):
        if (value.startswith("0x")):
            return int(value, base=16)
        elif (value.isdigit()):
            return int(value)
        return value
    def config (self):
        while True:
            inp = input("config> ")
            if (inp == "QUIT"):
                break
            if (inp.startswith("set")):
                l = inp.split(" ", 2)
                if (l[1].upper() in self.conf):
                    self.conf[l[1].upper()] = self._parse(l[2])
                    if (l[1].upper() == "PROT"):
                        self.conf["NIB"] = e.protocols[0xadfc]["data"][e.protocols[0xadfc]["versions"][0]]["nibd"]
            elif (inp.startswith("get")):
                l = inp.split(" ", 1)
                if (l[1].upper() in self.conf):
                    v = self.conf[l[1].upper()]
                    print(hex(v) if l[1].upper() == "PROT" else f"\"{v}\"" if type(v) == str else v)
    def _getcheck (self):
        pid = self.conf["PROT"]
        if (type(pid) == str):
            pid = e.namemap[pid]
        vid = e.protocols[pid]["versions"][self.conf["VERS"]]
        if (pid == 0xadfc):
            return e.protocols[pid]["data"][vid]["tree"]
        elif (pid == 0xa7e4):
            return e.protocols[pid]["data"][vid]["table"]
    def _encode_find_missing (self, inp):
        inp = list(inp)
        check = self._getcheck()
        for i in range(len(inp)):
            inp[i] = inp[i] if inp[i] in check else "???"
        return "".join(inp)
    def _validate_encode (self, inp):
        check = self._getcheck()
        for c in inp:
            if (c not in check):
                return False
        return True
    def _validate_decode (self, inp):
        for c in inp:
            if (c not in "0123456789abcdef"):
                return False
        return True
    def encode (self):
        while True:
            inp = input("encode> ")
            if (inp == "QUIT"):
                break
            if (len(inp) > 1 and inp[0] == "\\" and not inp[1] == "\\"):
                inp = inp[1:]
            inp = self._encode_find_missing(inp)
            if (self._validate_encode(inp)):
                prot = e.protocols[self.conf["PROT"]]
                prot = prot["data"][prot["versions"][self.conf["VERS"]]]
                prot["nib"] = self.conf["NIB"]
                print(e.encode(self.conf["PROT"], self.conf["VERS"], inp))
            else:
                print("BOGUS")
    def decode (self):
        while True:
            inp = input("decode> ")
            if (inp == "QUIT"):
                break
            if (self._validate_decode(inp)):
                print(e.decode(inp))
    def interface (self):
        while True:
            inp = input("> ")
            if (inp == "QUIT"):
                break
            if (inp == "help"):
                self.help()
            if (inp == "config"):
                self.config()
            if (inp == "encode"):
                self.encode()
            if (inp == "decode"):
                self.decode()

if (len(script_args) > 1):
    if (script_args[1] == "-i"):
        Interface()