# MultiBungeeBans

### System requirements
- You'll need a mysql or mariadb database
- You'll need a redis database with a **configured** password!

### Setup
- On first plugin start, a directory named "MultiBungeeBans" with the config file inside is going to be created.
- Fill in your correct database connection information.
- Restart all your proxies which have this plugin.
- You're done!

### Commands with Permissions
- `/bancheck [Player/UUID/BanID]` - `multibans.bancheck` to check a current ban of a player. 
- `/banlist <[Page]>` - `multibans.banlist` to check which players are currently banned from the network.
- `/gban [Player] [Reason]` - `multibans.ban` to ban a player across multiple proxies permanently from the network.
- `/tempban [Player] [TimeFormat] [BanReason]` - `multibans.tempban` to temporarily ban players across multiple proxies from the network.
- `/unban [Player/UUID/BanID]` - `multibans.unban` to unban a player from the network.
