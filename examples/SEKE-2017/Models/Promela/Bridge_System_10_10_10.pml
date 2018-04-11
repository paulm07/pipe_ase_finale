#define BOUND 20


#define routeA(p1) place_routeA??[eval(p1)]
#define waitA(p1) place_waitA??[eval(p1)]
#define controller(p1) place_controller??[eval(p1)]
#define computer(p1) place_computer??[eval(p1)]
#define choice(p1) place_choice??[eval(p1)]
#define range(p1) place_range??[eval(p1)]
#define capacity(p1) place_capacity??[eval(p1)]
#define onBridgeA(p1) place_onBridgeA??[eval(p1)]
#define exitA(p1) place_exitA??[eval(p1)]
#define routeB(p1) place_routeB??[eval(p1)]
#define waitB(p1) place_waitB??[eval(p1)]
#define onBridgeB(p1) place_onBridgeB??[eval(p1)]
#define exitB(p1) place_exitB??[eval(p1)]
#define nbA(p1) place_nbA??[eval(p1)]
#define nbB(p1) place_nbB??[eval(p1)]

typedef Type_1 {
  byte field1;
};

Type_1 var_Type_1;

typedef Type_2 {
  byte field1;
};

Type_2 var_Type_2;

chan place_routeA = [BOUND] of {Type_1};
chan place_waitA = [BOUND] of {Type_1};
chan place_controller = [BOUND] of {Type_2};
chan place_computer = [BOUND] of {Type_2};
chan place_choice = [BOUND] of {Type_2};
chan place_range = [BOUND] of {Type_2};
chan place_capacity = [BOUND] of {Type_2};
chan place_onBridgeA = [BOUND] of {Type_1};
chan place_exitA = [BOUND] of {Type_1};
chan place_routeB = [BOUND] of {Type_1};
chan place_waitB = [BOUND] of {Type_1};
chan place_onBridgeB = [BOUND] of {Type_1};
chan place_exitB = [BOUND] of {Type_1};
chan place_nbA = [BOUND] of {Type_2};
chan place_nbB = [BOUND] of {Type_2};

inline clean(source, var) {
  do
  :: nempty(source) -> source?var
  od
}

inline copy(source, destination, var) {
  for (var in source) {
    destination!var;
  }
}

inline transfer(source, dest, var) {
  do
  :: nempty(source) -> source?var; dest!var;
  od
}

int ID_regA = 1;
int ID_authA = 2;
int ID_T10 = 4;
int ID_leaveA = 8;
int ID_regB = 16;
int ID_AuthB = 32;
int ID_leaveB = 64;
int ID_timeoutA = 128;
int ID_timeoutB = 256;
int ID_decide = 512;
int ID_alternate = 1024;
int allEnabled = 2047;
int enabledAny = 2047;
inline disable(setBit) {
  enabledAny = enabledAny&~setBit;
  isEnabledAny = enabledAny>0;
}

inline enableAll() {
  enabledAny = allEnabled;
  isEnabledAny = true;
}

bool isEnabledAny = true;
inline is_enabled_regA() {
  for (x in place_routeA) {
    for (cA in place_nbA) {
      if
      :: (true) -> enableAll(); fire_regA_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_regA);
  endfire:
}

inline fire_regA_0() {
  place_routeA??eval(x.field1);
  place_nbA??eval(cA.field1);

  cA1.field1 = cA.field1 + 1;
;

  place_nbA!cA1.field1;
  place_waitA!x.field1;

}
inline regA() {
  
  Type_2 cA1;
  Type_1 x;
  Type_2 cA;
  
  atomic {
    is_enabled_regA();
  }
}

inline is_enabled_authA() {
  for (s in place_controller) {
    for (x in place_waitA) {
      for (cA in place_nbA) {
        for (n in place_capacity) {
          if
          :: (s.field1 == 1 && cA.field1 > 0 && n.field1 > 0) -> enableAll(); fire_authA_0(); goto endfire;
          :: else -> skip;
          fi

        }

      }

    }

  }
  disable(ID_authA);
  endfire:
}

inline fire_authA_0() {
  place_controller??eval(s.field1);
  place_waitA??eval(x.field1);
  place_nbA??eval(cA.field1);
  place_capacity??eval(n.field1);

  cA1.field1 = cA.field1 - 1;
  n1.field1 = n.field1 - 1;
;

  place_nbA!cA1.field1;
  place_choice!s.field1;
  place_capacity!n1.field1;
  place_onBridgeA!x.field1;

}
inline authA() {
  
  Type_2 cA1;
  Type_2 s;
  Type_2 n1;
  Type_1 x;
  Type_2 cA;
  Type_2 n;
  
  atomic {
    is_enabled_authA();
  }
}

inline is_enabled_T10() {
  for (s in place_range) {
    for (n in place_capacity) {
      if
      :: (n.field1 == 10) -> enableAll(); fire_T10_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_T10);
  endfire:
}

inline fire_T10_0() {
  place_range??eval(s.field1);
  place_capacity??eval(n.field1);
  place_choice!s.field1;
  place_capacity!n.field1;

}
inline T10() {
  
  Type_2 s;
  Type_2 n;
  
  atomic {
    is_enabled_T10();
  }
}

inline is_enabled_leaveA() {
  for (x in place_onBridgeA) {
    for (n in place_capacity) {
      if
      :: (true) -> enableAll(); fire_leaveA_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_leaveA);
  endfire:
}

inline fire_leaveA_0() {
  place_onBridgeA??eval(x.field1);
  place_capacity??eval(n.field1);

  n1.field1 = n.field1 + 1;
;

  place_capacity!n1.field1;
  place_exitA!x.field1;

}
inline leaveA() {
  
  Type_2 n1;
  Type_1 x;
  Type_2 n;
  
  atomic {
    is_enabled_leaveA();
  }
}

inline is_enabled_regB() {
  for (x in place_routeB) {
    for (cB in place_nbB) {
      if
      :: (true) -> enableAll(); fire_regB_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_regB);
  endfire:
}

inline fire_regB_0() {
  place_routeB??eval(x.field1);
  place_nbB??eval(cB.field1);

  cB1.field1 = cB.field1 + 1;
;

  place_nbB!cB1.field1;
  place_waitB!x.field1;

}
inline regB() {
  
  Type_2 cB1;
  Type_1 x;
  Type_2 cB;
  
  atomic {
    is_enabled_regB();
  }
}

inline is_enabled_AuthB() {
  for (s in place_controller) {
    for (x in place_waitB) {
      for (n in place_capacity) {
        for (cB in place_nbB) {
          if
          :: (s.field1 == 2 && cB.field1 > 0 && n.field1 > 0) -> enableAll(); fire_AuthB_0(); goto endfire;
          :: else -> skip;
          fi

        }

      }

    }

  }
  disable(ID_AuthB);
  endfire:
}

inline fire_AuthB_0() {
  place_controller??eval(s.field1);
  place_waitB??eval(x.field1);
  place_capacity??eval(n.field1);
  place_nbB??eval(cB.field1);

  cB1.field1 = cB.field1 - 1;
  n1.field1 = n.field1 - 1;
;

  place_choice!s.field1;
  place_nbB!cB1.field1;
  place_capacity!n1.field1;
  place_onBridgeB!x.field1;

}
inline AuthB() {
  
  Type_2 s;
  Type_2 cB1;
  Type_2 n1;
  Type_1 x;
  Type_2 n;
  Type_2 cB;
  
  atomic {
    is_enabled_AuthB();
  }
}

inline is_enabled_leaveB() {
  for (x in place_onBridgeB) {
    for (n in place_capacity) {
      if
      :: (true) -> enableAll(); fire_leaveB_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_leaveB);
  endfire:
}

inline fire_leaveB_0() {
  place_onBridgeB??eval(x.field1);
  place_capacity??eval(n.field1);

  n1.field1 = n.field1 + 1;
;

  place_capacity!n1.field1;
  place_exitB!x.field1;

}
inline leaveB() {
  
  Type_2 n1;
  Type_1 x;
  Type_2 n;
  
  atomic {
    is_enabled_leaveB();
  }
}

inline is_enabled_timeoutA() {
  for (s in place_controller) {
    for (cA in place_nbA) {
      for (cB in place_nbB) {
        if
        :: (s.field1 == 1 && cA.field1 == 0 && cB.field1 > 0) -> enableAll(); fire_timeoutA_0(); goto endfire;
        :: else -> skip;
        fi

      }

    }

  }
  disable(ID_timeoutA);
  endfire:
}

inline fire_timeoutA_0() {
  place_controller??eval(s.field1);
  place_nbA??eval(cA.field1);
  place_nbB??eval(cB.field1);

  s1.field1 = 2;
  c0.field1 = 0;
;

  place_computer!c0.field1;
  place_nbA!cA.field1;
  place_range!s1.field1;
  place_nbB!cB.field1;

}
inline timeoutA() {
  
  Type_2 s;
  Type_2 c0;
  Type_2 cA;
  Type_2 s1;
  Type_2 cB;
  
  atomic {
    is_enabled_timeoutA();
  }
}

inline is_enabled_timeoutB() {
  for (s in place_controller) {
    for (cA in place_nbA) {
      for (cB in place_nbB) {
        if
        :: (s.field1 == 2 && cB.field1 == 0 && cA.field1 > 0) -> enableAll(); fire_timeoutB_0(); goto endfire;
        :: else -> skip;
        fi

      }

    }

  }
  disable(ID_timeoutB);
  endfire:
}

inline fire_timeoutB_0() {
  place_controller??eval(s.field1);
  place_nbA??eval(cA.field1);
  place_nbB??eval(cB.field1);

  s1.field1 = 1;
  c0.field1 = 0;
;

  place_computer!c0.field1;
  place_nbA!cA.field1;
  place_range!s1.field1;
  place_nbB!cB.field1;

}
inline timeoutB() {
  
  Type_2 s;
  Type_2 c0;
  Type_2 cA;
  Type_2 s1;
  Type_2 cB;
  
  atomic {
    is_enabled_timeoutB();
  }
}

inline is_enabled_decide() {
  for (s in place_choice) {
    for (cpt in place_computer) {
      if
      :: (cpt.field1 < 10) -> enableAll(); fire_decide_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_decide);
  endfire:
}

inline fire_decide_0() {
  place_choice??eval(s.field1);
  place_computer??eval(cpt.field1);

  cpt1.field1 = cpt.field1 + 1;
;

  place_controller!s.field1;
  place_computer!cpt1.field1;

}
inline decide() {
  
  Type_2 s;
  Type_2 cpt1;
  Type_2 cpt;
  
  atomic {
    is_enabled_decide();
  }
}

inline is_enabled_alternate() {
  for (s in place_choice) {
    for (cpt in place_computer) {
      if
      :: (cpt.field1 == 10 && s.field1 == 1) -> enableAll(); fire_alternate_0(); goto endfire;
      :: (cpt.field1 == 10 && s.field1 == 2) -> enableAll(); fire_alternate_1(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_alternate);
  endfire:
}

inline fire_alternate_0() {
  place_choice??eval(s.field1);
  place_computer??eval(cpt.field1);

  c0.field1 = 0;
  s1.field1 = 2;
;

  place_computer!c0.field1;
  place_range!s1.field1;

}
inline fire_alternate_1() {
  place_choice??eval(s.field1);
  place_computer??eval(cpt.field1);

  c0.field1 = 0;
  s1.field1 = 1;
;

  place_computer!c0.field1;
  place_range!s1.field1;

}
inline alternate() {
  
  Type_2 s;
  Type_2 cpt;
  Type_2 c0;
  Type_2 s1;
  
  atomic {
    is_enabled_alternate();
  }
}

proctype Main() {

  do
  :: isEnabledAny -> regA()
  :: isEnabledAny -> authA()
  :: isEnabledAny -> T10()
  :: isEnabledAny -> leaveA()
  :: isEnabledAny -> regB()
  :: isEnabledAny -> AuthB()
  :: isEnabledAny -> leaveB()
  :: isEnabledAny -> timeoutA()
  :: isEnabledAny -> timeoutB()
  :: isEnabledAny -> decide()
  :: isEnabledAny -> alternate()
  :: !isEnabledAny -> break;
  od
}

init {
  place_routeA!191;
  place_routeA!689;
  place_routeA!92;
  place_routeA!192;
  place_routeA!426;
  place_routeA!306;
  place_routeA!267;
  place_routeA!413;
  place_routeA!850;
  place_routeA!814;



  place_computer!0;

  place_choice!1;


  place_capacity!10;



  place_routeB!908;
  place_routeB!816;
  place_routeB!602;
  place_routeB!414;
  place_routeB!382;
  place_routeB!355;
  place_routeB!416;
  place_routeB!14;
  place_routeB!516;
  place_routeB!827;




  place_nbA!0;

  place_nbB!0;

  run Main()
}


int x; 
ltl f1 { []!((len(place_onBridgeA) > 2 || len(place_onBridgeB)) > 2) } 
ltl f2 { [] !( len(place_onBridgeA) > 0 && len(place_onBridgeB)>0) } 
ltl f3 { [] (routeA(x) -> <> exitA(x)) } 
ltl f4 {(routeA(850) -> <> exitA(850))} 
ltl f5 {(routeB(355) -> <> exitB(355))}

