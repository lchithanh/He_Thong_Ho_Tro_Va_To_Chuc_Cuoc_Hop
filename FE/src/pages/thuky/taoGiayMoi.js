import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'

export const KIEU_TEP = {
  pdf: 'application/pdf',
  docx: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
}

function chuanBiVanBan(cuocHop) {
  if (!cuocHop.tieuDe || !cuocHop.thoiGianBatDau || !cuocHop.thoiGianKetThuc || !cuocHop.diaDiem) {
    throw new Error('Chưa có lịch đã xác nhận của kế hoạch đã duyệt để tạo giấy mời.')
  }
  if (!cuocHop.nguoiNhan) throw new Error('Nhập hoặc chọn người được mời trước khi tạo giấy mời.')

  return {
    coQuan: import.meta.env?.VITE_TEN_CO_QUAN || '',
    trichYeu: cuocHop.tieuDe,
    noiDung: [
      `Kính mời: ${cuocHop.nguoiNhan}`,
      `Nội dung họp: ${cuocHop.noiDung || 'Chưa ghi nội dung'}`,
      cuocHop.chuTri && `Chủ trì: ${cuocHop.chuTri}`,
      `Thời gian: ${dinhDangNgayGio(cuocHop.thoiGianBatDau)} đến ${dinhDangNgayGio(cuocHop.thoiGianKetThuc)}`,
      `Địa điểm/Hình thức: ${cuocHop.diaDiem}`,
    ].filter(Boolean),
  }
}

const QUOC_HIEU = 'CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM'
const TIEU_NGU = 'Độc lập - Tự do - Hạnh phúc'

async function taoWord(giay) {
  const {
    AlignmentType, BorderStyle, Document, Packer, Paragraph, Table, TableCell, TableRow, TextRun, WidthType,
  } = await import('docx')
  const giua = AlignmentType.CENTER
  const doan = (text, tuyChon = {}) => new Paragraph({
    alignment: tuyChon.can,
    spacing: { after: tuyChon.khoangCach ?? 105 },
    children: text.split('\n').map((dong, i) => new TextRun({
      text: dong, break: i ? 1 : undefined, bold: tuyChon.dam, italics: tuyChon.nghieng, size: tuyChon.coChu,
    })),
  })
  const bang = (trai, phai) => new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    borders: Object.fromEntries(['top', 'bottom', 'left', 'right', 'insideHorizontal', 'insideVertical']
      .map((canh) => [canh, { style: BorderStyle.NONE }])),
    rows: [new TableRow({
      children: [trai, phai].map((children, i) => new TableCell({
        width: { size: i ? 56 : 44, type: WidthType.PERCENTAGE }, children,
      })),
    })],
  })
  const docx = new Document({
    styles: { default: { document: { run: { font: 'Times New Roman', size: 26 } } } },
    sections: [{
      properties: { page: { size: { width: 11906, height: 16838 }, margin: { top: 1134, bottom: 1134, left: 1701, right: 1134 } } },
      children: [
        bang(
          [doan(giay.coQuan, { can: giua, dam: true, coChu: 24 })],
          [doan(QUOC_HIEU, { can: giua, dam: true, coChu: 24 }), doan(TIEU_NGU, { can: giua, dam: true })],
        ),
        doan(''),
        doan('GIẤY MỜI', { dam: true, can: giua, coChu: 30 }),
        doan(giay.trichYeu, { dam: true, can: giua, khoangCach: 260 }),
        ...giay.noiDung.map((dong) => doan(dong)),
        doan(''),
        doan('Bản chưa ký', { nghieng: true, can: AlignmentType.RIGHT }),
      ],
    }],
  })
  return Packer.toBlob(docx)
}

async function taoPdf(giay) {
  const [
    { default: pdfMake }, { default: phanPhoiFont },
  ] = await Promise.all([import('pdfmake/build/pdfmake.js'), import('pdfmake/build/vfs_fonts.js')])
  pdfMake.addVirtualFileSystem(phanPhoiFont)
  const vanBan = (text, khac = {}) => ({ text, margin: [0, 0, 0, 7], ...khac })
  const canGiua = { alignment: 'center' }
  const dinhDang = {
    pageSize: 'A4',
    pageMargins: [85, 57, 57, 57],
    defaultStyle: { fontSize: 12 },
    content: [
      { columns: [
        { width: '44%', ...vanBan(giay.coQuan, { ...canGiua, fontSize: 11, bold: true }) },
        { width: '56%', stack: [
          vanBan(QUOC_HIEU, { ...canGiua, bold: true, fontSize: 11 }),
          vanBan(TIEU_NGU, { ...canGiua, bold: true }),
        ] },
      ] },
      vanBan('GIẤY MỜI', { ...canGiua, fontSize: 15, bold: true, margin: [0, 16, 0, 6] }),
      vanBan(giay.trichYeu, { ...canGiua, bold: true, margin: [0, 0, 0, 16] }),
      ...giay.noiDung.map((dong) => vanBan(dong)),
      vanBan('Bản chưa ký', { alignment: 'right', italics: true, margin: [0, 12, 0, 0] }),
    ],
  }
  return new Promise((resolve) => {
    pdfMake.createPdf(dinhDang).getBlob(resolve)
  })
}

const TOI_DA = 2 * 1024 * 1024

export async function layTepGiayMoi(duLieu, cuocHop) {
  const cheDo = duLieu.get('tepGiayMoi')
  if (!cheDo || cheDo === 'KHONG_KEM') return null
  if (cheDo === 'CO_SAN') {
    const tep = duLieu.get('tepGiayMoiCoSan')
    const dinhDang = tep?.name?.split('.').at(-1)?.toLowerCase()
    if (!Object.hasOwn(KIEU_TEP, dinhDang) || !tep.size || tep.size > TOI_DA || tep.name.length > 150) {
      throw new Error('Chọn tệp PDF hoặc Word .docx hợp lệ, tối đa 2 MB và tên tệp không quá 150 ký tự.')
    }
    return { tep, tenTep: tep.name, dinhDang }
  }
  const dinhDang = cheDo === 'PDF' ? 'pdf' : 'docx'
  const giay = chuanBiVanBan(cuocHop)
  const tep = await (dinhDang === 'pdf' ? taoPdf(giay) : taoWord(giay))
  if (tep.size > TOI_DA) throw new Error('Tệp giấy mời vượt quá 2 MB.')
  return { tep, tenTep: 'Giay-moi-hop.' + dinhDang, dinhDang }
}

export async function themTepVaoThuMoi(thuMoi, duLieu, cuocHop) {
  const giayMoi = await layTepGiayMoi(duLieu, cuocHop)
  if (!giayMoi) return thuMoi
  const { tep, tenTep } = giayMoi
  const duLieuBase64 = await new Promise((resolve, reject) => {
    const docTep = new FileReader()
    docTep.onload = () => resolve(docTep.result.split(',')[1])
    docTep.onerror = () => reject(new Error('Không đọc được tệp giấy mời.'))
    docTep.readAsDataURL(tep)
  })
  const lichHopId = duLieu.get('tepGiayMoi') === 'CO_SAN' ? undefined : cuocHop.lichHopId
  return { ...thuMoi, tepDinhKem: { tenTep, duLieuBase64, lichHopId } }
}

export function thongTinGiayMoi(cuocHop, nguoiNhan, linkHop) {
  return { ...cuocHop, nguoiNhan, diaDiem: linkHop || cuocHop?.diaDiem }
}

export function tenNguoiNhanGiayMoi(nguoi) {
  return [nguoi.hoTen, nguoi.chucVu, nguoi.tenPhongBan || nguoi.donViCongTac, nguoi.truSo].filter(Boolean).join(', ')
}
