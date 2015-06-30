# lejos-server
TCP server program on EV3, JSON communication

WARNING: THIS PROGRAM IS STILL IN DEVELOPMENT


Default sensor modes:
RGB mode for color sensor
Distance mode for distance sensor

In case sensor is replaced, program needs to be restarted.

Program accepts JSON over TCP port 6789. Each command must be one line (new line - new command) so JSON needs to be minimized (white space removed).

NOTES: 
- don't worry if the led of the color sensor doesn't change color when you set different mode - it will do so immediately before executing getvalue
- IR sensor in distance mode works in range 5-50 cm, below 5 cm returns 0, above - null.
- touch sensor returns only 0.0 or 1.0

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

If you want to change camera resolution (although only 160x120 and 176x144 seem to be supported), first CLOSE the camera and initialize again.
