# Prism [![Build Status](https://api.travis-ci.org/prism/Prism.png)](https://travis-ci.org/prism/Prism/)

*By viveleroi*

Prism is a rollback/restore grief-prevention plugin for Sponge-based Minecraft servers. 

![Prism](http://helion3.s3.amazonaws.com/prism.jpg)

Prism 3.x has been completely rewritten for Sponge. It's still very early in the development process but we're working hard to exceed even our own standards.

Because of this, our feature set is growing. File an issue or let us know in IRC what's important to you.

## Installation

1. Place into your `mods` directory. Prism (3 Alpha) currenty only supports **MongoDB**. 
2. Ensure your Prism configuration file has the proper database connection details.

## Features

- Unprecedented **block change tracking**. We've worked closing with the Sponge team to ensure it provides us the framework we need.
- Lookup & "near" commands.
- Rollback and Restore commands.
- "Undo" command.
- MongoDB support.

## Planned/Coming Soon

- H2/MySQL Support
- Entity event tracking.
- Item/inventory event tracking.
- Support for additional databases.

## Commands

- `/pr l (parameters)` - Lookup records, filtering by parameters.

## Parameters

- `a:(event)` - "Action". `break`, `place`, etc.
- `before:(time)` - "Before" time period.
- `since:(time)` - "Since" time period.
- `player:(name)` - "Player" name. May be an offline player.
- `r:(number)` - "Radius" - A distance around you.
- `b:(block id)` - "Block" name. Like "grass".

## Flags

- `drain` - Drain liquids within a rollback area.
- `--no-group` - Prevent record grouping in lookups.

## Permissions

- `prism.lookup` - Can query records (via lookup or near).
- `prism.rollback` - Can perform a rollback.
- `prism.override.radius` - Can exceed the maximum radius in the config.

## API

Prism 3 offers an API which allows you to:

- Query the database.
- Register custom action handlers (allow custom parameter parsing).
- Register custom result handlers (allow custom rollback/restores).
- ... and more!

Documentation Pending.

## IRC

Please follow prism development on `irc.esper.net` in `#prism`

## Credits

Prism 3 is the successor to Prism 1/2 (for Bukkit/Spigot/Cauldron). While much of the code has been rewritten it's still influenced by our original vision.

- viveleroi (*Creator, Lead Dev*)
- Dev Testing: Wolfire1, Ollie2000, ickyacky

#### Prism 1.x - 2.x

- bloodmc (*Assistance with MCPC+ special compatibility*)
- nasonfish (*Contributor*)
- YeaItsMe (*Release QA*)
- nasonfish, Natman93, YeaItsMe, mafoan (*Alpha Testers*)
- mafoan, randox24, tacovan, nehocbelac, Shampoo123, cxmmy14, Palczynski, drac17, ollie2000, PGKxNIGHTMARE, allies333, DocVanNostrand, drfizzman123, 00benallen, rachaelriott, PheonixTamer, YeaItsMe, Natman93, Brazter, sydney2005, rsleight, napalm1, Teh_Fishman, and plenty more from DHMC (*Live Testers on DHMC*)
- Artwork by [LegendarySoldier](http://legendary-soldier.deviantart.com/)

## Donate to Vive

[![alt text][2]][1]

  [1]: https://www.paypal.com/cgi-bin/webscr?return=http%3A%2F%2Fdev.bukkit.org%2Fserver-mods%2Fprism%2F&cn=Add+special+instructions+to+the+addon+author%28s%29&business=botsko%40gmail.com&bn=PP-DonationsBF%3Abtn_donateCC_LG.gif%3ANonHosted&cancel_return=http%3A%2F%2Fdev.bukkit.org%2Fserver-mods%2Fprism%2F&lc=US&item_name=Prism+%28from+Bukkit.org%29&cmd=_donations&rm=1&no_shipping=1&currency_code=USD
  [2]: http://botsko.s3.amazonaws.com/paypal_donate.gif

Hey, I'm **viveleroi** and I'm responsible for 99% of Prism, the WebUI, the website, the documentation, responding to comments, IRC, and snowy tickets. But I also have a job and a family. Prism, and the rest of my plugins take an incalculable amount of time and that's hard to manage without any pay.

So please, *make a donation and make it easier for me to continue with these amazing plugins*.



### Compiling

Use the provided Gradle runtime to compile.

    ./gradlew build

### Development

- `git clone git@github.com:prism/Prism.git`
- `cp scripts/pre-commit .git/hooks`