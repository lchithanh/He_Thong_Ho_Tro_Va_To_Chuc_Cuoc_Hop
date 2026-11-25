export default function LienKetDeXuat({ noiDung = '' }) {
  const cacDong = noiDung.split(/\r?\n/).map((dong) => dong.trim()).filter(Boolean)

  return (
    <ul className="space-y-1">
      {cacDong.map((dong, i) => {
        const url = URL.canParse(dong) ? new URL(dong) : null
        const hopLe = url && ['https:', 'http:'].includes(url.protocol)

        return (
          <li key={i} className="wrap-anywhere">
            {hopLe ? (
              <a href={url.href} target="_blank" rel="noopener noreferrer"
                className="text-blue-800 underline">
                {dong}
              </a>
            ) : dong}
          </li>
        )
      })}
    </ul>
  )
}