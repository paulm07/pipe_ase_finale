#define BOUND 40


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
bool isInitialized = false;
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
active proctype regA() {
  
  Type_2 cA1;
  Type_1 x;
  Type_2 cA;
  
  isInitialized == true;
  start_regA:
  atomic {
    is_enabled_regA();
    if
    :: !isEnabledAny -> goto end_regA;
    :: else -> skip;
    fi
  }
  goto start_regA;
  end_regA:
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
active proctype authA() {
  
  Type_2 cA1;
  Type_2 s;
  Type_2 n1;
  Type_1 x;
  Type_2 cA;
  Type_2 n;
  
  isInitialized == true;
  start_authA:
  atomic {
    is_enabled_authA();
    if
    :: !isEnabledAny -> goto end_authA;
    :: else -> skip;
    fi
  }
  goto start_authA;
  end_authA:
}

inline is_enabled_T10() {
  for (s in place_range) {
    for (n in place_capacity) {
      if
      :: (n.field1 == 20) -> enableAll(); fire_T10_0(); goto endfire;
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
active proctype T10() {
  
  Type_2 s;
  Type_2 n;
  
  isInitialized == true;
  start_T10:
  atomic {
    is_enabled_T10();
    if
    :: !isEnabledAny -> goto end_T10;
    :: else -> skip;
    fi
  }
  goto start_T10;
  end_T10:
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
active proctype leaveA() {
  
  Type_2 n1;
  Type_1 x;
  Type_2 n;
  
  isInitialized == true;
  start_leaveA:
  atomic {
    is_enabled_leaveA();
    if
    :: !isEnabledAny -> goto end_leaveA;
    :: else -> skip;
    fi
  }
  goto start_leaveA;
  end_leaveA:
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
active proctype regB() {
  
  Type_2 cB1;
  Type_1 x;
  Type_2 cB;
  
  isInitialized == true;
  start_regB:
  atomic {
    is_enabled_regB();
    if
    :: !isEnabledAny -> goto end_regB;
    :: else -> skip;
    fi
  }
  goto start_regB;
  end_regB:
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
active proctype AuthB() {
  
  Type_2 s;
  Type_2 cB1;
  Type_2 n1;
  Type_1 x;
  Type_2 n;
  Type_2 cB;
  
  isInitialized == true;
  start_AuthB:
  atomic {
    is_enabled_AuthB();
    if
    :: !isEnabledAny -> goto end_AuthB;
    :: else -> skip;
    fi
  }
  goto start_AuthB;
  end_AuthB:
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
active proctype leaveB() {
  
  Type_2 n1;
  Type_1 x;
  Type_2 n;
  
  isInitialized == true;
  start_leaveB:
  atomic {
    is_enabled_leaveB();
    if
    :: !isEnabledAny -> goto end_leaveB;
    :: else -> skip;
    fi
  }
  goto start_leaveB;
  end_leaveB:
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
active proctype timeoutA() {
  
  Type_2 s;
  Type_2 c0;
  Type_2 cA;
  Type_2 s1;
  Type_2 cB;
  
  isInitialized == true;
  start_timeoutA:
  atomic {
    is_enabled_timeoutA();
    if
    :: !isEnabledAny -> goto end_timeoutA;
    :: else -> skip;
    fi
  }
  goto start_timeoutA;
  end_timeoutA:
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
active proctype timeoutB() {
  
  Type_2 s;
  Type_2 c0;
  Type_2 cA;
  Type_2 s1;
  Type_2 cB;
  
  isInitialized == true;
  start_timeoutB:
  atomic {
    is_enabled_timeoutB();
    if
    :: !isEnabledAny -> goto end_timeoutB;
    :: else -> skip;
    fi
  }
  goto start_timeoutB;
  end_timeoutB:
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
active proctype decide() {
  
  Type_2 s;
  Type_2 cpt1;
  Type_2 cpt;
  
  isInitialized == true;
  start_decide:
  atomic {
    is_enabled_decide();
    if
    :: !isEnabledAny -> goto end_decide;
    :: else -> skip;
    fi
  }
  goto start_decide;
  end_decide:
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
active proctype alternate() {
  
  Type_2 s;
  Type_2 cpt;
  Type_2 c0;
  Type_2 s1;
  
  isInitialized == true;
  start_alternate:
  atomic {
    is_enabled_alternate();
    if
    :: !isEnabledAny -> goto end_alternate;
    :: else -> skip;
    fi
  }
  goto start_alternate;
  end_alternate:
}

proctype Main() {
  printf("main");
}

init {
  place_routeA!400;
  place_routeA!128;
  place_routeA!123;
  place_routeA!297;
  place_routeA!823;
  place_routeA!178;
  place_routeA!752;
  place_routeA!675;
  place_routeA!366;
  place_routeA!719;
  place_routeA!978;
  place_routeA!798;
  place_routeA!586;
  place_routeA!465;
  place_routeA!224;
  place_routeA!192;
  place_routeA!833;
  place_routeA!857;
  place_routeA!699;
  place_routeA!97;



  place_computer!0;

  place_choice!1;


  place_capacity!20;



  place_routeB!878;
  place_routeB!794;
  place_routeB!47;
  place_routeB!655;
  place_routeB!146;
  place_routeB!273;
  place_routeB!754;
  place_routeB!45;
  place_routeB!234;
  place_routeB!74;
  place_routeB!991;
  place_routeB!707;
  place_routeB!240;
  place_routeB!322;
  place_routeB!316;
  place_routeB!589;
  place_routeB!383;
  place_routeB!944;
  place_routeB!29;
  place_routeB!217;




  place_nbA!0;

  place_nbB!0;

  isInitialized = true;
  run Main()
}

int x;  
ltl f1 { []!((len(place_onBridgeA)>10 || len(place_onBridgeB)) > 10) }  
ltl f2 { [] !( len(place_onBridgeA) > 0 && len(place_onBridgeB)>0) }  
ltl f3 { [] (routeA(x) -> <> exitA(x)) }  
ltl f4 {(routeA(465) -> <> exitA(465))}  
ltl f5 {(routeB(589) -> <> exitB(589))} }
