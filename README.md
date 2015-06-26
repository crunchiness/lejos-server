# lejos-server
TCP server program on EV3, JSON communication

WARNING: THIS PROGRAM IS STILL IN DEVELOPMENT


Default sensor modes:
RGB mode for color sensor
Distance mode for distance sensor

In case sensor is replaced, program needs to be restarted.

Program accepts JSON over TCP port 6789. Each command must be one line (new line - new command) so JSON needs to be minimized (white space removed).

API:
example command:
{"command": {"cmd": <COMMAND>,"dev": <DEVICE>}}
{"command": {"cmd": "beep","dev": "brick"}}

COMMAND = command name
DEVICE = device name ("motor", "sensor", "brick")

List of commands:
general commands:
"exit"
brick commands:
"beep", "buzz"
motor commands:
"init", "forward", "backward", "stop", "getspeed", "setspeed", "resettacho"
sensor commands:
"init", "getvalue", "setmode"
